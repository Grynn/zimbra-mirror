
/**
 * <p>UI Helper class to provide the necessary UI components.</p>
 *
 * @class
 * @static
 *
 * @author Mohammed Shaik Hussain Ali
 *
 * @this {ZaUIHelper}
 *
 */
ZaUIHelper = function() {}

/**
 * <p>Returns the next unique id by adding the prefix.</p>
 *
 * @static
 * @return {string} next unique it
 *
 */
ZaUIHelper.getItemUUid = function () {
    var prefix = "ZaItem"; // TODO: update
    return Dwt.getNextId(prefix);
}

/**
 * <p>Boolean options.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.BOOLEAN_OPTIONS = [
    {value: true,   label: ZaMsg.label_yes},
    {value: false,  label: ZaMsg.label_no},
    {value: null,   label: ZaMsg.label_no}
];

/**
 * <p>Boolean options as strings.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.BOOLEAN_AS_STRING_OPTIONS = [
    {value: "TRUE",     label: ZaMsg.label_yes},
    {value: "FALSE",    label: ZaMsg.label_no},
    {value: null,       label: ZaMsg.label_no}
];

/**
 * <p>Boolean options as numbers.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.BOOLEAN_AS_NUMBER_OPTIONS = [
    {value: "1",    label: ZaMsg.label_yes},
    {value: "0",    label: ZaMsg.label_no},
    {value: null,   label: ZaMsg.label_no}
];

/**
 * <p>Font sizes from 8pt to 36pt in 'pt' mode.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.FONT_SIZE_IN_PT_OPTIONS = [
    {value: "8pt",  label: "8pt"},
    {value: "10pt", label: "10pt"},
    {value: "12pt", label: "12pt"},
    {value: "14pt", label: "14pt"},
    {value: "18pt", label: "18pt"},
    {value: "24pt", label: "24pt"},
    {value: "36pt", label: "36pt"}
];

/**
 * <p>Font family options.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.FONT_FAMILY_OPTIONS = [
    {value: "arial, helvetica, sans-serif",                         label: ZaMsg.label_fontFamily_SansSerif},
    {value: "times new roman, new york, times, serif",              label: ZaMsg.label_fontFamily_Serif},
    {value: "arial black, avant garde",                             label: ZaMsg.label_fontFamily_WideBlock},
    {value: "courier new, courier, monaco, monospace, sans-serif",  label: ZaMsg.label_fontFamily_Monospaced},
    {value: "comic sans ms, comic sans, sans-serif",                label: ZaMsg.label_fontFamily_Comic},
    {value: "lucida console, sans-serif",                           label: ZaMsg.label_fontFamily_Console},
    {value: "garamond, new york, times, serif",                     label: ZaMsg.label_fontFamily_Garamond},
    {value: "georgia, serif",                                       label: ZaMsg.label_fontFamily_Elegant},
    {value: "tahoma, new york, times, serif",                       label: ZaMsg.label_fontFamily_Professional},
    {value: "terminal, monaco",                                     label: ZaMsg.label_fontFamily_Terminal},
    {value: "trebuchet ms, sans-serif",                             label: ZaMsg.label_fontFamily_Modern},
    {value: "verdana, helvetica, sans-serif",                       label: ZaMsg.label_fontFamily_Wide}
];

/**
 * <p>Compose mail format options.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.COMPOSE_MAIL_FORMAT_OPTIONS = [
    {value: "text", label: ZaMsg.label_text},
    {value: "html", label: ZaMsg.label_html}
];

/**
 * <p>Send read receipt options.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.SEND_READ_RECEIPT_OPTIONS = [
    {value: "always",   label: ZaMsg.label_always},
    {value: "never",    label: ZaMsg.label_never},
    {value: "prompt",   label: ZaMsg.label_prompt}
];

/**
 * <p>Group mail by options.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.GROUP_MAIL_BY_OPTIONS = [
    {value: "conversation", label: ZaMsg.label_conversation},
    {value: "message",      label: ZaMsg.label_message}
];

/**
 * <p>Reminder options in minutes.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.REMINDER_OPTIONS = [
    {value: 0,  label: ZaMsg.label_never},
    {value: 1,  label: "1"},
    {value: 5,  label: "5"},
    {value: 10, label: "10"},
    {value: 15, label: "15"},
    {value: 20, label: "20"},
    {value: 25, label: "25"},
    {value: 30, label: "30"},
    {value: 45, label: "45"},
    {value: 50, label: "50"},
    {value: 55, label: "55"},
    {value: 60, label: "60"}
];

/**
 * <p>Defines the current step.</p>
 *
 * @static
 * @type {string}
 *
 */
ZaUIHelper.CURRENT_STEP = "currentStep";

/**
 * <p>Defines the current tab.</p>
 *
 * @static
 * @type {string}
 *
 */
ZaUIHelper.CURRENT_TAB = "currentTab";

/**
 * <p>Time options with days, hours, minutes and seconds.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.TIME_DHMS_OPTIONS = [
    {value: "d",    label: AjxMsg.days},
    {value: "h",    label: AjxMsg.hours},
    {value: "m",    label: AjxMsg.minutes},
    {value: "s",    label: AjxMsg.seconds}
];

/**
 * <p>Time options with days and hours.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.TIME_DH_OPTIONS = [
    {value: "d",    label: AjxMsg.days},
    {value: "h",    label: AjxMsg.hours}
];

/**
 * <p>Time options with days, weeks, months and years.</p>
 *
 * @static
 * @type {Array}
 *
 */
ZaUIHelper.TIME_DWMY_OPTIONS = [
    {value: "d",    label: AjxMsg.days},
    {value: "w",    label: AjxMsg.weeks},
    {value: "m",    label: AjxMsg.months},
    {value: "y",    label: AjxMsg.years}
];

/**
 * TODO: Fill up
 *
 * <p></p>
 *
 * @static
 * @param v
 * @param choices
 * @return {*}
 *
 */
ZaUIHelper.setUnrecognizedChoiceValue = function(v, choices) {
    var new_v = ZaMsg.value_unrecognized;
    var myChoices = choices;

    if (typeof choices === "function") {
        myChoices = choices.call(this);
    }

    for (var i = 0; i < myChoices.length; i++) {
        if (v == myChoices[i].value) {
            new_v = v;
            break;
        }
    }

    return new_v;
}
