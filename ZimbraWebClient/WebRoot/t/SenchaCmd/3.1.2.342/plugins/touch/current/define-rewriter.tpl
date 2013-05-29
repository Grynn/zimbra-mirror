(Ext.cmd.derive({classNameStr}, {baseClassName}, {config},
    
    <tpl if="enumerableMembers"> 
        {enumerableMembers}
    <tpl else>
        0
    </tpl>,
    
    <tpl if="xtypes"> 
        [
            <tpl for="xtypes" between=",">
                "{.}"
            </tpl>
        ]
    <tpl else>
        0
    </tpl>,
    
    <tpl if="xtypesChain"> 
        [
            <tpl for="xtypesChain" between=",">
                "{.}"
            </tpl>
        ],
        {
            <tpl for="xtypesChain" between=",">
                "{.}": true
            </tpl>
        }
    <tpl else>
        0, 0
    </tpl>,
        
    <tpl if="aliases"> 
        [
            <tpl for="aliases" between=",">
                "{.}"
            </tpl>
        ]
    <tpl else>
        0
    </tpl>,
    
    <tpl if="mixins"> 
        [<tpl for="mixins" between=",">['{name}', {value}]</tpl>]
    <tpl elseif="mixinLiterals">
        [<tpl for="mixinLiterals" between=",">
            [{.}.prototype.mixinId || {.}.$className, {.}]
        </tpl>]
    <tpl else>
        0
    </tpl>,
    
    <tpl if="names"> 
        [
            <tpl for="names" between=",">
                {.}
            </tpl>
        ]
    <tpl else>
        0
    </tpl>,
    
    <tpl if="createdFn"> 
        {createdFn}
    <tpl else>
        0
    </tpl>
    )
)
