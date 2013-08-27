/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

ZaModel = function(init) {
 	if (arguments.length == 0) return;
	this._evtMgr = new AjxEventMgr();
}

ZaModel.getBooleanChoices = function () {
    return [
        {
            value: "TRUE",
            label: ZaMsg.Yes
        },
        {
            value: "FALSE",
            label: ZaMsg.No
        },
        {
            value: null,
            label: ZaMsg.No
        }
    ];
}

ZaModel.BOOLEAN_CHOICES= ZaModel.getBooleanChoices ;

ZaModel.getBooleanChoices1 = function () {
    return [
        {
            value: true,
            label: ZaMsg.Yes
        },
        {
            value: false,
            label: ZaMsg.No
        },
        {
            value: null,
            label: ZaMsg.No
        }
    ];
}

ZaModel.BOOLEAN_CHOICES1= ZaModel.getBooleanChoices1 ;

ZaModel.getBooleanChoices2 = function () {
    return [
        {
            value: "1",
            label: ZaMsg.Yes
        },
        {
            value: "0",
            label: ZaMsg.No
        },
        {
            value: null,
            label: ZaMsg.No
        }
    ];
}

ZaModel.BOOLEAN_CHOICES2= ZaModel.getBooleanChoices2 ;

ZaModel.getAllowChoices = function () {
	return [
        {
            value: "1",
            label: ZaMsg.Yes
        },
        {
            value: "0",
            label: ZaMsg.No
        },
        {
            value: "-1",
            label: ZaMsg.Ignored
        },
        {
            value: null,
            label: ZaMsg.No
        }
    ];
}

ZaModel.ALLOW_CHOICES = ZaModel.getAllowChoices;

ZaModel.FONT_SIZE_CHOICES = [
    {
        value: "8pt",
        label: "8pt"
    },
    {
        value: "10pt",
        label: "10pt"
    },
    {
        value: "12pt",
        label: "12pt"
    },
    {
        value: "14pt",
        label: "14pt"
    },
    {
        value: "18pt",
        label: "18pt"
    },
    {
        value: "24pt",
        label: "24pt"
    },
    {
        value: "36pt",
        label: "36pt"
    }
];

ZaModel.getFontFamilyChoices = function() {
	return [
        {
            label: ZaMsg.LBL_fontFamilySansSerif,
            value: "arial, helvetica, sans-serif"
        },
        {
            label: ZaMsg.LBL_fontFamilySerif,
            value: "times new roman, new york, times, serif"
        },
        {
            label: ZaMsg.LBL_fontFamilyWideBlock,
            value: "arial black,avant garde"
        },
        {
            label: ZaMsg.LBL_fontFamilyMonospaced,
            value: "courier new, courier, monaco, monospace, sans-serif"
        },
        {
            label: ZaMsg.LBL_fontFamilyComic,
            value: "comic sans ms, comic sans, sans-serif"
        },
        {
            label: ZaMsg.LBL_fontFamilyConsole,
            value: "lucida console, sans-serif"
        },
        {
            label: ZaMsg.LBL_fontFamilyGaramond,
            value: "garamond, new york, times, serif"
        },
        {
            label: ZaMsg.LBL_fontFamilyElegant,
            value: "georgia,serif"
        },
        {
            label: ZaMsg.LBL_fontFamilyProfessional,
            value: "tahoma, new york, times, serif"
        },
        {
            label: ZaMsg.LBL_fontFamilyTerminal,
            value: "terminal,monaco"
        },
        {
            label: ZaMsg.LBL_fontFamilyModern,
            value: "trebuchet ms,sans-serif"
        },
        {
            label: ZaMsg.LBL_fontFamilyWide,
            value: "verdana, helvetica, sans-serif"
        }
    ];
}

ZaModel.FONT_FAMILY_CHOICES = ZaModel.getFontFamilyChoices;

ZaModel.getComposeFormatChoices =   function () {
    return [
        {
            value: "text",
            label: ZaMsg.Text
        },
        {
            value: "html",
            label: ZaMsg.HTML
        }
    ];
}

ZaModel.COMPOSE_FORMAT_CHOICES = ZaModel.getComposeFormatChoices ;

ZaModel.SEND_READ_RECEIPT_ALWAYS = "always";
ZaModel.SEND_READ_RECEIPT_NEVER = "never";
ZaModel.SEND_READ_RECEIPT_PROMPT = "prompt";

ZaModel.getSendReadReceiptByChoices = function() {
    return [
        {
            value: ZaModel.SEND_READ_RECEIPT_ALWAYS,
            label: ZaMsg.SEND_READ_RECEIPT_ALWAYS
        },
        {
            value: ZaModel.SEND_READ_RECEIPT_NEVER,
            label: ZaMsg.SEND_READ_RECEIPT_NEVER
        },
        {
            value: ZaModel.SEND_READ_RECEIPT_PROMPT,
            label: ZaMsg.SEND_READ_RECEIPT_PROMPT
        }
    ];
}

ZaModel.SEND_READ_RECEPIT_CHOICES = ZaModel.getSendReadReceiptByChoices;

ZaModel.getGroupMailByChoices = function () {
    return [
        {
            value: "conversation",
            label: ZaMsg.Conversation
        },
        {
            value: "message",
            label: ZaMsg.Message
        }
    ];
}

ZaModel.GROUP_MAIL_BY_CHOICES = ZaModel.getGroupMailByChoices ;

ZaModel.getSignatureStyleChoices = function () {
    return [
        {
            value: "outlook",
            label: ZaMsg.No
        },
        {
            value: "internet",
            label: ZaMsg.Yes
        }
    ];
}

ZaModel.SIGNATURE_STYLE_CHOICES = ZaModel.getSignatureStyleChoices ;

ZaModel.getReminderChoices = function () {
    return [
        {
            value: "0",
            label: ZaMsg.never
        },
        {
            value: 1,
            label: "1"
        },
        {
            value: 5,
            label: "5"
        },
        {
            value: 10,
            label: "10"
        },
        {
            value: 15,
            label: "15"
        },
        {
            value: 20,
            label: "20"
        },
        {
            value: 25,
            label: "25"
        },
        {
            value: 30,
            label: "30"
        },
        {
            value: 45,
            label: "45"
        },
        {
            value: 50,
            label: "50"
        },
        {
            value: 55,
            label: "55"
        },
        {
            value: 60,
            label: "60"
        }
    ];
}

ZaModel.REMINDER_CHOICES = ZaModel.getReminderChoices ;

ZaModel.ErrorCode = "code";

ZaModel.ErrorMessage = "error_message";

ZaModel.currentStep = "currentStep";

ZaModel.currentTab = "currentTab";

ZaModel.getTimeChoices = function () {
    return [
        {
            value: "d",
            label: AjxMsg.days
        },
        {
            value: "h",
            label: AjxMsg.hours
        },
        {
            value: "m",
            label: AjxMsg.minutes
        },
        {
            value: "s",
            label: AjxMsg.seconds
        }
    ];
}

ZaModel.getTimeChoices1 = function () {
    return [
        {
            value: "d",
            label: AjxMsg.days
        },
        {
            value: "h",
            label: AjxMsg.hours
        }
    ];
}

ZaModel.getLongTimeChoices = function () {
    return [
        {
            value: "d",
            label: AjxMsg.days
        },
        {
            value: "w",
            label: AjxMsg.weeks
        },
        {
            value: "m",
            label: AjxMsg.months
        },
        {
            value: "y",
            label: AjxMsg.years
        }
    ];
}

/**
 * The length of one year is used to indicate manual choice
 * @returns {Array}
 */
ZaModel.getMailPollingIntervalChoices = function () {
    return [
        {
            value: "500ms",
            label: ZaMsg.LBL_asNewMailArrives
        },
        {
            value: "120s",
            label: "2 " + ZaMsg.LBL_minute
        },
        {
            value: "180s",
            label: "3 " + ZaMsg.LBL_minute
        },
        {
            value: "240s",
            label: "4 " + ZaMsg.LBL_minute
        },
        {
            value: "300s",
            label: "5 " + ZaMsg.LBL_minute
        },
        {
            value: "360s",
            label: "6 " + ZaMsg.LBL_minute
        },
        {
            value: "420s",
            label: "7 " + ZaMsg.LBL_minute
        },
        {
            value: "480s",
            label: "8 " + ZaMsg.LBL_minute
        },
        {
            value: "540s",
            label: "9 " + ZaMsg.LBL_minute
        },
        {
            value: "600s",
            label: "10 " + ZaMsg.LBL_minute
        },
        {
            value: "900s",
            label: "15 " + ZaMsg.LBL_minute
        },
        {
            value: "31536000s",
            label: ZaMsg.LBL_manual
        }
    ];
}

ZaModel.MAIL_POLLING_INTERVAL_CHOICES = ZaModel.getMailPollingIntervalChoices;

ZaModel.prototype.toString = function() {
    return "ZaModel";
}

ZaModel.prototype.addChangeListener = function(listener) {
    return this._evtMgr.addListener(ZaEvent.L_MODIFY, listener);
}

ZaModel.prototype.removeChangeListener = function(listener) {
    return this._evtMgr.removeListener(ZaEvent.L_MODIFY, listener);
}

ZaModel.setUnrecoganizedChoiceValue = function (v, choices) {
    var new_v = ZaMsg.VALUE_UNRECOGNIZED;
    var myChoices = choices ;
    if (typeof(choices) == "function") {
        myChoices = choices.call (this) ;
    }
    for (var i = 0; i < myChoices.length; i++) {
        if (v == myChoices[i].value) {
            new_v = v ;
            break ;
        }
    }
    return new_v ;
}