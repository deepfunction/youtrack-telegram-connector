var entities = require('@jetbrains/youtrack-scripting-api/entities');
var http = require('@jetbrains/youtrack-scripting-api/http');
var workflow = require('@jetbrains/youtrack-scripting-api/workflow');

function addMessage(messages, message) {
    // Проверяем есть ли уже такие сообщения подготовленные к отправке
    var exist = false;
    if (messages.length > 0) {
        for (var value in messages) {
            if (JSON.stringify(messages[value]) === JSON.stringify(message)) {
                exist = true;
            }
        }
        if (!exist) {
            messages.push(message);
        }
    } else {
        messages.push(message);
    }
}

function findUserInText(isNew, assigned, watcher, updater, issueText, issue, where, messages) {
    var issueLink = '[' + issue.id + "](" + issue.url + ')';
    // Если упомянули кого-то
    var loginFromText;
    var message;
    if (issueText != null && issueText.includes('@')) {
        loginFromText = issueText.split('@').pop().split(' ')[0];
    }
    if (loginFromText !== undefined) {
        var currentText = loginFromText + ", тебя упомянули в " + where + " к задаче " + issue.summary + " " + issueLink;
        message = {
            login: loginFromText,
            text: currentText
        };
        addMessage(messages, message);
    }
    // Если комментарий без упоминаний
    if (!isNew && loginFromText === undefined) {
        if (assigned !== undefined && assigned !== updater.login) {
            message = {
                login: assigned,
                text: issueText
            };
            addMessage(messages, message);
        }
        if (watcher !== undefined && watcher !== updater.login) {
            message = {
                login: watcher,
                text: issueText
            };
            addMessage(messages, message);
        }
    }
}

function formMessage(isNew, assigned, watcher, text, changedByName, issueLink, issue, summary, message, updater, messages) {
    // Если новая задача
    if (isNew) {
        if (assigned !== undefined && assigned !== issue.reporter.login) {
            text = assigned + ", на тебя была назначена новая задача" + "\n" +
                "Назначил: " + changedByName + "\n" +
                "Ссылка: " + issueLink + "\n" +
                "Состояние: " + issue.fields.State.presentation + "\n" +
                "Приоритет: " + issue.fields.Priority.presentation + "\n" +
                "Название: " + summary;
            message = {
                login: assigned,
                text: text
            };
            addMessage(messages, message);
        }
        var issueText = issue.description;
        findUserInText(true, assigned, watcher, updater, issueText, issue, "описании", messages);
    }
    // Обновление
    if (!isNew) {
        var isNewComment = issue.comments.isChanged;
        if (isNewComment) {
            text = "К задаче " + issueLink + " был добавлен новый комментарий:\n";
            var comments = issue.comments;
            comments.forEach(function (comment) {
                if (comment.isNew) {
                    // Добавляем в текст сообщения текст комментария
                    text += "\n" + comment.text;
                    findUserInText(false, assigned, watcher, updater, text, issue, "комментарии", messages);
                }
            });
        } else {
            // Если обновлена не тем на кого назначена
            if (assigned !== undefined && updater.login !== assigned) {
                text = assigned + ", задача " + issue.summary + " " + issueLink + " была обновлена.";
                message = {
                    login: assigned,
                    text: text
                };
                addMessage(messages, message);
            }
            if (watcher !== undefined && updater.login !== watcher) {
                text = watcher + ", задача " + issue.summary + " " + issueLink + " была обновлена.";
                message = {
                    login: watcher,
                    text: text
                };
                addMessage(messages, message);
            }
            // Отправляем создателю, что задача закрыта
            if (issue.State.presentation === 'Done' && issue.reporter.login !== updater.login && issue.fields.oldValue('State') !== null && issue.fields.oldValue('State').presentation !== 'Done') {
                text = issue.reporter.login + ", задача " + issue.summary + " " + issueLink + " закрыта.";
                message = {
                    login: issue.reporter.login,
                    text: text
                };
                addMessage(messages, message);
            }
        }
    }
}

exports.rule = entities.Issue.onChange({
    title: workflow.i18n('Send notification to telegram when an issue is changed or commented.'),
    guard: function (ctx) {
        // Если был оставлен комментарий или изменилось описание задачи, или добавлен тэг
        return ctx.issue.tags.added.isNotEmpty() || !ctx.issue.comments.added.isEmpty() || ctx.issue.isResolved || ctx.issue.isReported || ctx.issue.becomesUnresolved;
    },
    action: function (ctx) {
        var issue = ctx.issue;
        var issueLink = '[' + issue.id + "](" + issue.url + ')';
        var summary;
        var isNew = false;
        if (issue.becomesReported) {
            isNew = true;
        }
        summary = issue.summary;
        var changedByName = '';
        var assignee = ctx.issue.fields.Assignee;
        var updater;
        var assigned;
        var watcher;
        var message;
        var text = "";
        var assignedUsers = [];
        var watchers = [];
        var messages = [];

        // Добавляем в отдельный лист тех на кого назначили задачу
        if (assignee !== undefined) {
            assignee.forEach(function (value) {
                assignedUsers.push(value.login);
            });
        }

        // Добавляем в отдельный лист подписанных (watchers)
        if (issue.tags.size > 0) {
            issue.tags.forEach(function (value) {
                if (value.name === "Star") {
                    var watcher = value.owner;
                    watchers.push(watcher.login);
                }
            });
        }

        if (isNew) {
            changedByName = issue.reporter.fullName;
            updater = issue.reporter;
        } else {
            changedByName = issue.updatedBy.fullName;
            updater = issue.updatedBy;
        }

        if (assignedUsers.length > 0) {
            for (var assignedUser in assignedUsers) {
                assigned = assignedUsers[assignedUser];
                if (watchers.length > 0) {
                    for (var watcherUser in watchers) {
                        watcher = watchers[watcherUser];
                        formMessage(isNew, assigned, watcher, text, changedByName, issueLink, issue, summary, message, updater, messages);
                    }
                }
                formMessage(isNew, assigned, watcher, text, changedByName, issueLink, issue, summary, message, updater, messages);
            }
        } else {
            // В этом случае ищем упоминание в комментарии
            formMessage(isNew, assigned, watcher, text, changedByName, issueLink, issue, summary, message, updater, messages);
        }

        if (messages.length > 0) {
            var connection = new http.Connection('http://localhost:8099', null, 500);
            connection.addHeader('Content-Type', 'application/json');
            connection.addHeader('Accept', 'application/json');
            connection.addHeader('Accept-Charset', 'UTF-8');
            for (message in messages) {
                var queryParams = {
                    login: messages[message].login,
                    text: messages[message].text
                };
                console.log(queryParams);
                var response = connection.postSync('/medsoft/youtrack/telegram/api/v1/youtrack/webhook', queryParams, []);
                console.log(response);
            }
        }
    },
    requirements: {}
});
