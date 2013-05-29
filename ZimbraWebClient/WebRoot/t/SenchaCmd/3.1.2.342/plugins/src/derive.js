// This code is common to Optimizer define rewriting for both touch and extjs
// 
// Ext.some.Base = Ext.derive('Ext.some.Base', Ext.Base, {}, null, null, ["base"], {"base": true}, ["foo.some.alias"]);

//@tag derive

(function (ExtCmd) {
    var baseStaticMembers = [],
        enumerables = [ 'constructor', 'toString', 'valueOf', 'toLocaleString' ],
        enumerablesMap = {},
        enumerablesObj = {},
        enumerablesMask = 0,
        baseStaticMember, Base, ClassManager, applyConfig,
        thunk = function () {
            var i, mask;
            
            Base = Ext.Base;
            ClassManager = Ext.ClassManager;
            
            for (i = enumerables.length; i-- > 0; ) {
                mask = (1 << i);
                
                enumerablesObj[enumerablesMap[mask] = enumerables[i]] = mask;
            }
            
            for (i in enumerablesObj) {
                enumerablesMask |= enumerablesObj[i];
            }
            
            enumerablesMask = ~enumerablesMask;
            
            Function.prototype.$isFunction = 1;
            
            applyConfig = Ext.Class.getPreprocessor('config').fn;
            
            for (baseStaticMember in Base) {
                if (Base.hasOwnProperty(baseStaticMember)) {
                    baseStaticMembers.push(baseStaticMember);
                }
            }
            
            ExtCmd.derive = derive;
            
            return derive.apply(this, arguments);
        },
        onBeforeCreated = function (cls, data, hooks) {
            var enumerableMembers = hooks.enumerableMembers,
                proto = cls.prototype, member, fn, which, val;
            
            if (!data) {
                return;
            }
            
            for (member in data) {
                val = data[member];
                
                if (val && val.$isFunction && !val.$isClass && val !== Ext.emptyFn &&
                    val !== Ext.identityFn) {
                    proto[member] = fn = val;

                    fn.$owner = cls;
                    fn.$name = member;
                } else {
                    proto[member] = val;
                }
            }
            
            for (which = 1; enumerableMembers; which <<= 1) {
                if (enumerableMembers & which) {
                    enumerableMembers &= ~which;
                    
                    member = enumerablesMap[which];
                    
                    proto[member] = fn = data[member];

                    fn.$owner = cls;
                    fn.$name = member;
                }
            }
        },
        makeShim = function (base) {
            var cls = function ctor() {
                // Opera has some problems returning from a constructor when Dragonfly isn't running. The || null seems to
                // be sufficient to stop it misbehaving. Known to be required against 10.53, 11.51 and 11.61.
                return base.apply(this, arguments) || null;
            }, i, name;
            
            cls.prototype = Ext.Object.chain(base.prototype);
            
            for (i = baseStaticMembers.length; i-- > 0;) {
                name = baseStaticMembers[i];
                cls[name] = Base[name];
            }
            
            return cls;
        },
        derive = function (className, base, data, enumerableMembers, 
                            xtypes, xtypesChain, xtypeMap,
                            aliases, mixins, names, createdFn) {
            var cls = function ctor() {
                    // Opera has some problems returning from a constructor when Dragonfly isn't running. The || null seems to
                    // be sufficient to stop it misbehaving. Known to be required against 10.53, 11.51 and 11.61.
                    return this.constructor.apply(this, arguments) || null;
                },
                ret = cls,
                hooks = {
                    enumerableMembers: enumerableMembers & enumerablesMask,
                    onCreated: createdFn,
                    onBeforeCreated: onBeforeCreated,
                    aliases: aliases
                },
                alternates = data.alternateClassName || [],
                global = Ext.global,
                alias, alternate, i, ln, n, ns, name, nameToAlternates, proto, statics,
                staticMember, targetName, fn, val;
            
            for (i = baseStaticMembers.length; i-- > 0;) {
                name = baseStaticMembers[i];
                cls[name] = Base[name];
            }
            
            if (data.$isFunction) {
                data = data(cls);
            }
            
            hooks.data = data;
            statics = data.statics,
            
            data.$className = className;
            
            if (data.$className) {
                cls.$className = data.$className;
            }
            
            cls.extend(base);
            
            proto = cls.prototype;
            
            cls.xtype = data.xtype = xtypes[0];
            
            if (xtypes) {
                proto.xtypes = xtypes;
            }
            
            proto.xtypesChain = xtypesChain;
            proto.xtypesMap = xtypeMap;
            
            data.alias = aliases;
            
            ret.triggerExtended(cls, data, hooks);
            
            if (data.onClassExtended) {
                cls.onExtended(data.onClassExtended, cls);
                delete data.onClassExtended;
            }
            
            if (statics) {
                for (staticMember in statics) {
                    if (statics.hasOwnProperty(staticMember)) {
                        val = statics[staticMember];

                        if (val && val.$isFunction && !val.$isClass && val !== Ext.emptyFn &&
                            val !== Ext.identityFn) {
                            cls[staticMember] = fn = val;

                            fn.$owner = cls;
                            fn.$name = staticMember;
                        }
                        cls[staticMember] = val;
                    }
                }
            }
            
            delete data.statics;

            if (data.inheritableStatics) {
                cls.addInheritableStatics(data.inheritableStatics);
            }
            
            if (proto.onClassExtended) {
                ret.onExtended(proto.onClassExtended, ret);
                delete proto.onClassExtended;
            }
            
            if (data.config) {
                applyConfig(cls, data);
            }
            
            hooks.onBeforeCreated(cls, hooks.data, hooks);
            
            for (i = 0, n = mixins && mixins.length; i < n; ++i) {
                cls.mixin.apply(cls, mixins[i]);
            }
            
            for (i = 0, n = aliases.length; i < n; i++) {
                alias = aliases[i];

                ClassManager.setAlias(cls, alias);
            }
            
            if (data.singleton) {
                ret = new cls();
            }
            
            if (!(alternates instanceof Array)) {
                alternates = [ alternates ];
            }
            
            for (i = 0, ln = alternates.length; i < ln; i++) {
                alternate = alternates[i];
                
                ClassManager.classes[alternate] = ret;
                
                targetName = ClassManager.getName(ret);
                nameToAlternates = ClassManager.maps.nameToAlternates;
                if (targetName && targetName !== alternate) {
                    ClassManager.maps.alternateToName[alternate] = targetName;
                    alternates = nameToAlternates[targetName] || (nameToAlternates[targetName] = []);
                    alternates.push(alternate);
                }
            }
            
            for (i = 0, n = names.length; i < n; i += 2) {
                ns = names[i];
                
                if (!ns) {
                    ns = global;
                }
                    
                ns[names[i + 1]] = ret;
            }

            ClassManager.classes[className] = ret;
            
            targetName = ClassManager.getName(ret);
            nameToAlternates = ClassManager.maps.nameToAlternates;
            if (targetName && targetName !== className) {
                ClassManager.maps.alternateToName[className] = targetName;
                alternates = nameToAlternates[targetName] || (nameToAlternates[targetName] = []);
                alternates.push(className);
            }
            
            delete proto.alternateClassName;
            
            if (hooks.onCreated) {
                hooks.onCreated.call(ret, ret);
            }
            
            if (className) {
                ClassManager.triggerCreated(className);
            }
        
            return ret;
        };
        
    ExtCmd.derive = thunk;
}(Ext.cmd = {}))
