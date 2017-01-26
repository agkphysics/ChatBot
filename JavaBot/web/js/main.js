/// <reference path="../typings/index.d.ts" />

function error(text) {
    Materialize.toast(text, 3000);
}

function appendMsg(text, person) {
    var template = $('#msgtemplate').clone();
    template.attr('id', messageCount);
    messageCount++;
    if (person === 'user') template.addClass('user')
    else template.addClass('bot');
    template.text(text);
    template.removeClass('hide');
    var msg = template.appendTo(messagesList);
    
    messagesList.animate({scrollTop: messagesList[0].scrollHeight}, 300);
    
    return msg;
}

var messageCount = 0;
var messageInput = $('#messageInput');
var spinner = $('#spinner');
var chatdiv = $('#chatdiv');
var messagesList = $('#messagesList');

$(function() {
    messageCount = messagesList.length;
    chatdiv.hide();
    
    var testBotReady = setInterval(function () {
        $.ajax('chat', {
            contents: {
                "status": "[0-9][0-9][0-9]",
                "result": ".*"
            },
            method: "GET"
        }).done(function (response) {
            if (response.result) {
                clearInterval(testBotReady);
                spinner.fadeOut(500, function () {
                    chatdiv.removeClass('hide');
                    chatdiv.fadeIn(500);
                });
                messageInput.removeAttr('disabled');
            }
        });
    }, 2000);

    $('#chatform').submit(function(e) {
        messageInput.attr('disabled', '');
        var messageText = messageInput.val();
        if (messageText.length === 0 || messageText.length > 100) {
            error("Please enter a message.")
        } else {
            messageText = messageText.replace('[][^*@`#><+=]', '');
            var message = appendMsg(messageText, 'user');
            $.ajax('chat', {
                contents: {
                    "status": "[0-9][0-9][0-9]",
                    "result": ".*"
                },
                data: {
                    c: messageText
                },
                cache: false,
                method: "POST"
            }).done(function(response) {
                appendMsg(response.result, 'bot');
                messageInput.removeAttr('disabled');
                messageInput.val('');
            }).fail(function(response) {
                message.remove();
                error("Sorry. An error occured.");
                messageInput.removeAttr('disabled');
            });
        }
        messageInput.removeAttr('disabled');
        return false;
    });

    $('#sendicon').click(function(e) {
        $('#chatform').submit();
    });
});
