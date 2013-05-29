//@require ../ant/ant-util.js
//@require ../ant/JSON.js

//@require Ext.js
//@require String.js
//@require Format.js
//@require Template.js
//@require XTemplateParser.js
//@require XTemplateCompiler.js
//@require XTemplate.js

(function (proto) {
    if (!proto.createArrayTest) {
        console.error("Something has changed in XTemplateCompiler");
        throw new Error("Something has changed in XTemplateCompiler");
    }

    proto.createArrayTest = ('isArray' in Array) ? function(L) {
        return 'Array.isArray(c' + L + ' = toJS(c' + L +'))';
    } : function(L) {
        return 'ts.call(c' + L + ' = toJS(c' + L + '))==="[object Array]"';
    };

}(Ext.XTemplateCompiler.prototype));
