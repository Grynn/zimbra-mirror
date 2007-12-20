package com.zimbra.cs.im.xp.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;
import java.text.MessageFormat;


import com.zimbra.cs.im.xp.tok.Buffer;
import com.zimbra.cs.im.xp.tok.ContentToken;
import com.zimbra.cs.im.xp.tok.EmptyTokenException;
import com.zimbra.cs.im.xp.tok.Encoding;
import com.zimbra.cs.im.xp.tok.EndOfPrologException;
import com.zimbra.cs.im.xp.tok.ExtensibleTokenException;
import com.zimbra.cs.im.xp.tok.InvalidTokenException;
import com.zimbra.cs.im.xp.tok.PartialTokenException;
import com.zimbra.cs.im.xp.tok.Position;
import com.zimbra.cs.im.xp.tok.PrologParser;
import com.zimbra.cs.im.xp.tok.PrologSyntaxException;
import com.zimbra.cs.im.xp.tok.StringConversionCache;
import com.zimbra.cs.im.xp.tok.TextDecl;
import com.zimbra.cs.im.xp.tok.Token;
import com.zimbra.cs.im.xp.tok.TokenException;
import com.zimbra.cs.im.xp.tok.XmlDecl;
import com.zimbra.cs.im.xp.util.Hashtable;

/**
 * Parses a single entity.
 * @version $Revision: 1.31 $ $Date: 1998/12/28 08:12:30 $
 */
public class EntityParser
extends ContentToken
implements StartElementEvent, EndElementEvent, CharacterDataEvent,
ProcessingInstructionEvent, EndPrologEvent,
CommentEvent, StartCdataSectionEvent, EndCdataSectionEvent,
StartEntityReferenceEvent, EndEntityReferenceEvent,
ParseLocation {

    private static final class EntityImpl implements Entity {
        byte[] text;
        String systemId;
        String publicId;
        URL baseURL;
        String notationName;
        boolean open;
        public String getSystemId() { return systemId; }
        public URL getBase() { return baseURL; }
        public String getPublicId() { return publicId; }
        public String getNotationName() { return notationName; }
        public String getReplacementText() {
            if (text == null)
                return null;
            StringBuffer buf = new StringBuffer(text.length >> 1);
            for (int i = 0; i < text.length; i += 2)
                buf.append((char)(((text[i] & 0xFF) << 8)
                            | (text[i + 1] & 0xFF)));
            return buf.toString();
        }
    }

    private static final class ElementTypeImpl implements ElementType {
        /* defaultIndex is equal to ID_DEFAULT_INDEX if the attribute
       is an ID attribute; this relies on the fact that ID attributes
       can't have defaults */
        static final int ID_DEFAULT_INDEX = -2;

        private static class Attribute implements AttributeDefinition {
            int defaultIndex = -1;
            boolean required = false;
            byte type = UNDECLARED;
            Vector values = null;
            String name = null;
            String value = null;
            String unnormalizedValue = null;

            public String getDefaultValue() {	return value; }
            public String getDefaultUnnormalizedValue() {
                return unnormalizedValue;
            }
            public boolean isRequired() { return required; }
            public byte getType() { return type; }
            public Enumeration allowedValues() {
                if (values == null)
                    return null;
                return values.elements();
            }
        }

        ElementTypeImpl() { }

        void setContentType(byte n) {
            contentType = n;
        }

        public byte getContentType() {
            return contentType;
        }

        void setContentSpec(String s) {
            contentSpec = s;
        }

        public String getContentSpec() {
            return contentSpec;
        }

        int getDefaultAttributeCount() {
            return nDefaultAtts;
        }

        int getAttributeDefaultIndex(String name) {
            Attribute att = (Attribute)attTable.get(name);
            return att == null ? -1 : att.defaultIndex;
        }

        String getDefaultAttributeValue(int i) {
            if (i >= nDefaultAtts)
                throw new IndexOutOfBoundsException();
            return defaultAtts[i].value;
        }

        String getDefaultAttributeUnnormalizedValue(int i) {
            if (i >= nDefaultAtts)
                throw new IndexOutOfBoundsException();
            return defaultAtts[i].unnormalizedValue;
        }

        String getDefaultAttributeName(int i) {
            if (i >= nDefaultAtts)
                throw new IndexOutOfBoundsException();
            return defaultAtts[i].name;
        }

        boolean isAttributeCdata(String name) {
            Attribute att = (Attribute)attTable.get(name);
            return att == null || att.type <= 0;
        }

        public Enumeration attributeNames() {
            return attTable.keys();
        }

        public AttributeDefinition getAttributeDefinition(String name) {
            return (AttributeDefinition)attTable.get(name);
        }

        /* Value may be null if the default is #IMPLIED or #REQUIRED. */
        boolean appendAttribute(String name,
                    String value,
                    String unnormalizedValue,
                    boolean required,
                    byte attributeType,
                    Vector allowedValues) {
            if (attTable.get(name) != null)
                return false;
            Attribute att = new Attribute();
            attTable.put(name, att);
            att.name = name;
            att.value = value;
            att.unnormalizedValue = unnormalizedValue;
            if (value == null)
                att.defaultIndex = -1;
            else {
                if (nDefaultAtts == defaultAtts.length) {
                    Attribute[] tem = defaultAtts;
                    defaultAtts = new Attribute[tem.length << 1];
                    System.arraycopy(tem, 0, defaultAtts, 0, tem.length);
                }
                defaultAtts[nDefaultAtts] = att;
                att.defaultIndex = nDefaultAtts++;
            }
            att.required = required;
            att.type = attributeType;
            if (attributeType == AttributeDefinition.ID && value == null)
                att.defaultIndex = ElementTypeImpl.ID_DEFAULT_INDEX;
            att.values = allowedValues;
            return true;
        }

        private final Hashtable attTable = new Hashtable();
        private int nDefaultAtts = 0;
        private Attribute[] defaultAtts = new Attribute[4];
        private byte contentType = ElementType.UNDECLARED_CONTENT;
        private String contentSpec = null;
    }

    private static class DTDImpl implements DTD {
        String name = null;
        EntityImpl externalSubset = null;
        URL baseURL = null;
        Hashtable elementTypeTable = new Hashtable();
        Hashtable generalEntityTable = new Hashtable();
        Hashtable paramEntityTable = new Hashtable();
        Hashtable notationTable = new Hashtable();
        boolean complete = true;
        boolean standalone = false;
        boolean hasInternalSubset = false;

        DTDImpl(URL baseURL) {
            this.baseURL = baseURL;
        }

        public String getDocumentTypeName() {
            return name;
        }

        public boolean isComplete() {
            return complete;
        }

        public boolean isStandalone() {
            return standalone;
        }

        public Enumeration entityNames(byte entityType) {
            switch (entityType) {
                case GENERAL_ENTITY:
                    return generalEntityTable.keys();
                case PARAMETER_ENTITY:
                    return paramEntityTable.keys();
                case NOTATION:
                    return notationTable.keys();
            }
            throw new IllegalArgumentException();
        }

        public Entity getEntity(byte entityType, String entityName) {
            switch (entityType) {
                case GENERAL_ENTITY:
                    return (Entity)generalEntityTable.get(entityName);
                case PARAMETER_ENTITY:
                    if (entityName.equals(DTD.EXTERNAL_SUBSET_NAME)) {
                        if (externalSubset.systemId == null)
                            return null;
                        else
                            return externalSubset;
                    }
                    return (Entity)paramEntityTable.get(entityName);
                case NOTATION:
                    return (Entity)notationTable.get(entityName);

            }
            throw new IllegalArgumentException();
        }

        public Enumeration elementTypeNames() {
            return elementTypeTable.keys();
        }

        public ElementType getElementType(String name) {
            return (ElementType)elementTypeTable.get(name);
        }
    }

    private static final boolean forceStandalone = false;

    private static final int READSIZE = 512;

    private static class StartExternalSubsetEvent
    implements StartEntityReferenceEvent {
        public String getName() { return DTD.EXTERNAL_SUBSET_NAME; }
    }

    private static final StartEntityReferenceEvent startExternalSubsetEvent
    = new StartExternalSubsetEvent();

    private EntityParser parent;
    private String internalEntityName;
    private boolean isParameterEntity;
    private byte[] buf;
    private int bufStart;
    private int bufEnd;
    private int currentTokenStart;
    private InputStream in;
    private URL baseURL;
    private String location;
    private Position pos = new Position();
    // The offset in buffer corresponding to pos.
    private int posOff = 0;
    private long bufEndStreamOffset = 0;
    private Encoding enc;
    // True if the encoding in the XML declaration should be ignored.
    private boolean ignoreDeclEnc;
    private /* final */ int minBPC;
    private int fixBPC;
    private StringConversionCache stringCache;
    private Encoding internalEnc;
    private StringConversionCache internalStringCache;
    private Application app;
    private DTDImpl dtd;
    private EntityManager entityManager;
    private Locale locale;

    private int nameStart;
    // Some temporary buffers
    private Buffer valueBuf;
    private char[] data;
    private static final int INIT_DATA_BUF_SIZE = 65;
    private int dataLength;
    private char[] dataBuf;
    private boolean dataIsRef = false;
    private String[] attValues;
    private String[] attNames;
    private int nAttributes;
    private int idAttributeIndex;
    private boolean[] defaultSpecified;
    
    public EntityParser(OpenEntity entity, EntityManager entityManager, Application app,
                Locale locale, EntityParser parent) throws IOException {
        this.in = entity.getInputStream();
        this.app = app;
        this.locale = locale;
        this.baseURL = entity.getBase();
        this.location = entity.getLocation();
        this.entityManager = entityManager;
        buf = new byte[READSIZE * 2];
        currentTokenStart = bufStart = bufEnd = 0;
        while (bufEnd - bufStart < 4 && fill())
            ;
        enc = Encoding.getInitialEncoding(buf, bufStart, bufEnd, this);
        currentTokenStart = bufStart = getTokenEnd();
        posOff = bufStart; // ignore the byte order mark in computing columns
        if (enc == null)
            fatal(MessageId.BAD_INITIAL_BYTES);
        String encName = entity.getEncoding();
        if (encName != null) {
            ignoreDeclEnc = true;
            enc = enc.getEncoding(encName);
            if (enc == null)
                fatal(MessageId.UNSUPPORTED_ENCODING);
        }
        minBPC = enc.getMinBytesPerChar();
        fixBPC = enc.getFixedBytesPerChar();
        stringCache = new StringConversionCache(enc);
        valueBuf = new Buffer();
        dataBuf = new char[INIT_DATA_BUF_SIZE];
        internalEnc = Encoding.getInternalEncoding();
        internalStringCache = new StringConversionCache(internalEnc);
        if (parent != null)
            dtd = parent.dtd;
        else
            dtd = new DTDImpl(baseURL);
    }
    
    public EntityParser(byte[] initialBuf, OpenEntity entity, EntityManager entityManager, Application app,
                Locale locale, EntityParser parent) throws IOException {
        this.in = entity.getInputStream();
        this.app = app;
        this.locale = locale;
        this.baseURL = entity.getBase();
        this.location = entity.getLocation();
        this.entityManager = entityManager;
        buf = initialBuf;
        currentTokenStart = bufStart = 0;
        bufEnd = buf.length;
        enc = Encoding.getInitialEncoding(buf, bufStart, bufEnd, this);
        currentTokenStart = bufStart = getTokenEnd();
        posOff = bufStart; // ignore the byte order mark in computing columns
        if (enc == null)
            fatal(MessageId.BAD_INITIAL_BYTES);
        String encName = entity.getEncoding();
        if (encName != null) {
            ignoreDeclEnc = true;
            enc = enc.getEncoding(encName);
            if (enc == null)
                fatal(MessageId.UNSUPPORTED_ENCODING);
        }
        minBPC = enc.getMinBytesPerChar();
        fixBPC = enc.getFixedBytesPerChar();
        stringCache = new StringConversionCache(enc);
        valueBuf = new Buffer();
        dataBuf = new char[INIT_DATA_BUF_SIZE];
        internalEnc = Encoding.getInternalEncoding();
        internalStringCache = new StringConversionCache(internalEnc);
        if (parent != null)
            dtd = parent.dtd;
        else
            dtd = new DTDImpl(baseURL);
    }
    

    private EntityParser(byte[] buf, String entityName, boolean isParameterEntity, EntityParser parent) {
        this.internalEntityName = entityName;
        this.isParameterEntity = isParameterEntity;
        this.buf = buf;
        this.parent = parent;
        baseURL = parent.baseURL;
        entityManager = parent.entityManager;
        currentTokenStart = bufStart = 0;
        bufEnd = buf.length;
        app = parent.app;
        locale = parent.locale;
        enc = internalEnc = parent.internalEnc;
        stringCache = internalStringCache = parent.internalStringCache;
        minBPC = enc.getMinBytesPerChar();
        fixBPC = enc.getFixedBytesPerChar();
        dtd = parent.dtd;
        valueBuf = parent.valueBuf;
        dataBuf = parent.dataBuf;
    }
    
    void parseDocumentEntity() throws IOException, ApplicationException {
        try {

            try {
                app.startDocument();
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ApplicationException(e);
            }
            parseDecls(PrologParser.PROLOG);
            parseContent(true, false);
            parseMisc();

            try {
                app.endDocument();
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ApplicationException(e);
            }
        }
        finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }
    
    private void parseExternalTextEntity() throws IOException, ApplicationException {
        try {
            for (;;) {
                try {
                    if (enc.tokenizeContent(buf, bufStart, bufEnd, this)
                                == Encoding.TOK_XML_DECL) {
                        currentTokenStart = bufStart;
                        bufStart = getTokenEnd();
                        handleXmlDecl(true);
                    }
                    break;
                }
                catch (InvalidTokenException e) {
                    break;
                }
                catch (TokenException e) {
                    if (!fill())
                        break;
                }
            }
            parseContent(false, false);
        }
        finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }

    private void handleXmlDecl(boolean notDocumentEntity) throws NotWellFormedException {
        try {
            TextDecl textDecl;
            if (notDocumentEntity)
                textDecl = new TextDecl(enc, buf, currentTokenStart, bufStart);
            else {
                XmlDecl xmlDecl = new XmlDecl(enc, buf, currentTokenStart, bufStart);
                dtd.standalone = xmlDecl.isStandalone();
                textDecl = xmlDecl;
            }
            if (!ignoreDeclEnc) {
                enc = enc.getEncoding(textDecl.getEncoding());
                if (enc == null)
                    fatal(MessageId.UNSUPPORTED_ENCODING);
                if (enc.getMinBytesPerChar() != minBPC)
                    fatal(MessageId.BAD_DECL_ENCODING);
                stringCache.setEncoding(enc);
                fixBPC = enc.getFixedBytesPerChar();
            }
        }
        catch (InvalidTokenException e) {
            currentTokenStart = e.getOffset();
            fatal(MessageId.INVALID_XML_DECLARATION);
        }
    }

    static class DeclState implements
    MarkupDeclarationEvent, StartDocumentTypeDeclarationEvent,
    EndDocumentTypeDeclarationEvent {
        DeclState(byte type, DTD dtd) {
            this.type = type;
            this.dtd = dtd;
        }
        public DTD getDTD() { return dtd; }
        public int getType() { return declType; }
        public String getName() { return declName; }
        public String getAttributeName() {
            if (declType != ATTRIBUTE)
                return null;
            return attributeName;
        }
        final byte type;
        final DTD dtd;
        EntityImpl entity;
        ElementTypeImpl elementType;
        String attributeName;
        byte attributeType;
        StringBuffer contentSpec = new StringBuffer();
        Vector allowedValues;
        String declName;
        int declType = -1;
    }

    public void parseDecls(byte type) throws IOException, ApplicationException {
        PrologParser pp = new PrologParser(type);
        DeclState declState = new DeclState(type, dtd);
        try {
            for (;;) {
                int tok;
                try {
                    tok = tokenizeProlog();
                }
                catch (EndOfPrologException e) {
                    if (type != PrologParser.PROLOG)
                        fatal(MessageId.SYNTAX_ERROR);
                    pp.end();
                    break;
                }
                catch (EmptyTokenException e) {
                    if (type == PrologParser.PROLOG) {
                        currentTokenStart = bufStart;
                        fatal(MessageId.NO_DOCUMENT_ELEMENT);
                    }
                    pp.end();
                    break;
                }
                prologAction(pp.action(tok, buf, currentTokenStart, bufStart, enc),
                            pp,
                            declState);
            }
        }
        catch (PrologSyntaxException e) {
            fatal(MessageId.SYNTAX_ERROR);
        }
        finally {
            if (type == PrologParser.EXTERNAL_ENTITY && in != null) {
                in.close();
                in = null;
            }
        }
        if (type == PrologParser.PROLOG) {
            try {
                app.endProlog(this);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ApplicationException(e);
            }
        }
    }

    void parseInnerParamEntity(PrologParser pp, DeclState declState) throws IOException, ApplicationException {
        int groupLevel = pp.getGroupLevel();
        try {
            for (;;) {
                int tok = tokenizeProlog();
                prologAction(pp.action(tok, buf, currentTokenStart, bufStart, enc),
                            pp,
                            declState);
                if (tok == Encoding.TOK_DECL_CLOSE)
                    fatal(MessageId.PE_DECL_NESTING);
            }
        }
        catch (EndOfPrologException e) {
            fatal(MessageId.SYNTAX_ERROR);
        }
        catch (PrologSyntaxException e) {
            fatal(MessageId.SYNTAX_ERROR);
        }
        catch (EmptyTokenException e) { }
        if (pp.getGroupLevel() != groupLevel)
            fatal(MessageId.PE_GROUP_NESTING);
    }

    void prologAction(int action, PrologParser pp, DeclState declState) throws IOException, ApplicationException {
        String name;
        switch (action) {
            case PrologParser.ACTION_XML_DECL:
                handleXmlDecl(false);
                break;
            case PrologParser.ACTION_TEXT_DECL:
                handleXmlDecl(true);
                break;
            case PrologParser.ACTION_ENTITY_PUBLIC_ID:
            case PrologParser.ACTION_DOCTYPE_PUBLIC_ID:
            case PrologParser.ACTION_NOTATION_PUBLIC_ID:
                if (declState.entity != null) {
                    try {
                        String id = enc.getPublicId(buf, currentTokenStart, bufStart);
                        declState.entity.publicId = id;
                    }
                    catch (InvalidTokenException e) {
                        currentTokenStart = e.getOffset();
                        fatal(MessageId.PUBID_CHAR);
                    }
                }
                break;
            case PrologParser.ACTION_DOCTYPE_NAME:
                dtd.name = stringCache.convert(buf, currentTokenStart, bufStart, true);
                dtd.externalSubset = new EntityImpl();
                declState.entity = dtd.externalSubset;
                break;
            case PrologParser.ACTION_NOTATION_NAME:
                declState.declType = MarkupDeclarationEvent.NOTATION;
                startEntityDecl(dtd.notationTable, declState);
                break;
            case PrologParser.ACTION_GENERAL_ENTITY_NAME:
                declState.declType = MarkupDeclarationEvent.GENERAL_ENTITY;
                startEntityDecl(dtd.generalEntityTable, declState);
                break;
            case PrologParser.ACTION_PARAM_ENTITY_NAME:
                declState.declType = MarkupDeclarationEvent.PARAMETER_ENTITY;
                startEntityDecl(dtd.paramEntityTable, declState);
                break;
            case PrologParser.ACTION_ENTITY_VALUE_NO_PEREFS:
            case PrologParser.ACTION_ENTITY_VALUE_WITH_PEREFS:
                byte[] text = makeReplacementText(action == PrologParser.ACTION_ENTITY_VALUE_WITH_PEREFS);
                if (declState.entity != null)
                    declState.entity.text = text;
                break;
            case PrologParser.ACTION_NOTATION_SYSTEM_ID:
            case PrologParser.ACTION_ENTITY_SYSTEM_ID:
            case PrologParser.ACTION_DOCTYPE_SYSTEM_ID:
                if (declState.entity != null) {
                    declState.entity.systemId
                    = stringCache.convert(buf,
                                currentTokenStart + minBPC,
                                bufStart - minBPC,
                                false);
                    declState.entity.baseURL = baseURL;
                }
                break;
            case PrologParser.ACTION_ENTITY_NOTATION_NAME:
                if (declState.entity != null)
                    declState.entity.notationName
                    = stringCache.convert(buf, currentTokenStart, bufStart, true);
                break;
            case PrologParser.ACTION_DOCTYPE_SUBSET:
                dtd.hasInternalSubset = true;
                reportStartDocumentTypeDeclaration(declState);
                break;
            case PrologParser.ACTION_DOCTYPE_CLOSE:
                if (!dtd.hasInternalSubset)
                    reportStartDocumentTypeDeclaration(declState);
                if (dtd.externalSubset != null
                            && dtd.externalSubset.systemId != null) {
                    if (!dtd.standalone && !forceStandalone) {
                        OpenEntity openEntity
                        = entityManager.open(dtd.externalSubset.systemId,
                                    baseURL,
                                    dtd.externalSubset.publicId);
                        if (openEntity != null) {
                            try {
                                app.startEntityReference(startExternalSubsetEvent);
                            }
                            catch (RuntimeException e) {
                                throw e;
                            }
                            catch (Exception e) {
                                throw new ApplicationException(e);
                            }
                            new EntityParser(openEntity, entityManager, app, locale, this)
                            .parseDecls(PrologParser.EXTERNAL_ENTITY);
                            reportEndEntityReference();
                            reportEndDocumentTypeDeclaration(declState);
                            return;
                        }
                    }
                    dtd.complete = false;
                }
                reportEndDocumentTypeDeclaration(declState);
                break;
            case PrologParser.ACTION_INNER_PARAM_ENTITY_REF:
            case PrologParser.ACTION_OUTER_PARAM_ENTITY_REF:
                nameStart = currentTokenStart + minBPC;
                name = stringCache.convert(buf,
                            nameStart,
                            getNameEnd(),
                            true);
                EntityImpl entity = (EntityImpl)dtd.paramEntityTable.get(name);
                if (entity == null) {
                    if (dtd.complete)
                        fatal(MessageId.UNDEF_PEREF, name);
                    break;
                }
                EntityParser parser = makeParserForEntity(entity, name, true);
                if (parser == null || dtd.standalone || forceStandalone) {
                    dtd.complete = false;
                    break;
                }
                entity.open = true;
                if (action == PrologParser.ACTION_OUTER_PARAM_ENTITY_REF) {
                    reportStartEntityReference();
                    parser.parseDecls(entity.text != null
                                ? PrologParser.INTERNAL_ENTITY
                                            : PrologParser.EXTERNAL_ENTITY);
                    reportEndEntityReference();
                }
                else
                    parser.parseInnerParamEntity(pp, declState);
                entity.open = false;
                break;
                /* Default attribute processing. */
            case PrologParser.ACTION_ATTLIST_ELEMENT_NAME:
                String gi = stringCache.convert(buf,
                            currentTokenStart,
                            bufStart,
                            true);
                declState.declType = MarkupDeclarationEvent.ATTRIBUTE;
                declState.declName = gi;
                declState.elementType = (ElementTypeImpl)dtd.elementTypeTable.get(gi);
                if (declState.elementType == null) {
                    declState.elementType = new ElementTypeImpl();
                    dtd.elementTypeTable.put(gi, declState.elementType);
                }
                break;
            case PrologParser.ACTION_ATTRIBUTE_NAME:
                declState.attributeName = stringCache.convert(buf,
                            currentTokenStart,
                            bufStart,
                            true);
                declState.allowedValues = null;
                break;
            case PrologParser.ACTION_ATTRIBUTE_TYPE_CDATA:
                declState.attributeType = AttributeDefinition.CDATA;
                break;
            case PrologParser.ACTION_ATTRIBUTE_TYPE_ID:
                declState.attributeType = AttributeDefinition.ID;
                break;
            case PrologParser.ACTION_ATTRIBUTE_TYPE_IDREF:
                declState.attributeType = AttributeDefinition.IDREF;
                break;
            case PrologParser.ACTION_ATTRIBUTE_TYPE_IDREFS:
                declState.attributeType = AttributeDefinition.IDREFS;
                break;
            case PrologParser.ACTION_ATTRIBUTE_TYPE_ENTITY:
                declState.attributeType = AttributeDefinition.ENTITY;
                break;
            case PrologParser.ACTION_ATTRIBUTE_TYPE_ENTITIES:
                declState.attributeType = AttributeDefinition.ENTITIES;
                break;
            case PrologParser.ACTION_ATTRIBUTE_TYPE_NMTOKEN:
                declState.attributeType = AttributeDefinition.NMTOKEN;
                break;
            case PrologParser.ACTION_ATTRIBUTE_TYPE_NMTOKENS:
                declState.attributeType = AttributeDefinition.NMTOKENS;
                break;
            case PrologParser.ACTION_IMPLIED_ATTRIBUTE_VALUE:
            case PrologParser.ACTION_REQUIRED_ATTRIBUTE_VALUE:
                if (declState
                            .elementType
                            .appendAttribute(declState.attributeName,
                                        null,
                                        null,
                                        action
                                        == PrologParser.ACTION_REQUIRED_ATTRIBUTE_VALUE,
                                        declState.attributeType,
                                        declState.allowedValues))
                    reportMarkupDeclaration(declState);
                break;
            case PrologParser.ACTION_DEFAULT_ATTRIBUTE_VALUE:
            case PrologParser.ACTION_FIXED_ATTRIBUTE_VALUE:
                if (declState
                            .elementType
                            .appendAttribute(declState.attributeName,
                                        makeAttributeValue(declState.attributeType
                                                    == AttributeDefinition.CDATA,
                                                    buf,
                                                    currentTokenStart + minBPC,
                                                    bufStart - minBPC),
                                                    normalizeNewlines(stringCache.convert(buf,
                                                                currentTokenStart + minBPC,
                                                                bufStart - minBPC,
                                                                false)),
                                                                action == PrologParser.ACTION_FIXED_ATTRIBUTE_VALUE,
                                                                declState.attributeType,
                                                                declState.allowedValues))
                    reportMarkupDeclaration(declState);
                break;
            case PrologParser.ACTION_ATTRIBUTE_ENUM_VALUE:
            case PrologParser.ACTION_ATTRIBUTE_NOTATION_VALUE:
                if (action == PrologParser.ACTION_ATTRIBUTE_NOTATION_VALUE)
                    declState.attributeType = AttributeDefinition.NOTATION;
                else
                    declState.attributeType = AttributeDefinition.ENUM;
                if (declState.allowedValues == null)
                    declState.allowedValues = new Vector();
                declState.allowedValues.addElement(stringCache.convert(buf,
                            currentTokenStart,
                            bufStart,
                            true));
                break;
            case PrologParser.ACTION_PI:
                nameStart = currentTokenStart + minBPC + minBPC;
                reportProcessingInstruction();
                break;
            case PrologParser.ACTION_COMMENT:
                reportComment();
                break;
            case PrologParser.ACTION_IGNORE_SECT:
                skipIgnoreSect();
                break;
            case PrologParser.ACTION_ELEMENT_NAME:
                name = stringCache.convert(buf, currentTokenStart, bufStart, true);
                declState.elementType = (ElementTypeImpl)dtd.elementTypeTable.get(name);
                if (declState.elementType == null) {
                    declState.elementType = new ElementTypeImpl();
                    dtd.elementTypeTable.put(name, declState.elementType);
                }
                declState.declName = name;
                declState.declType = MarkupDeclarationEvent.ELEMENT;
                declState.contentSpec.setLength(0);
                declState.elementType.setContentType(ElementType.ELEMENT_CONTENT);
                break;
            case PrologParser.ACTION_CONTENT_ANY:
                declState.elementType.setContentType(ElementType.ANY_CONTENT);
                declState.elementType.setContentSpec("ANY");
                break;
            case PrologParser.ACTION_CONTENT_EMPTY:
                declState.elementType.setContentType(ElementType.EMPTY_CONTENT);
                declState.elementType.setContentSpec("EMPTY");
                break;
            case PrologParser.ACTION_CONTENT_PCDATA:
                declState.elementType.setContentType(ElementType.MIXED_CONTENT);
                declState.contentSpec.append("#PCDATA");
                break;
            case PrologParser.ACTION_GROUP_OPEN:
            case PrologParser.ACTION_GROUP_CHOICE:
            case PrologParser.ACTION_GROUP_SEQUENCE:
            case PrologParser.ACTION_CONTENT_ELEMENT:
            case PrologParser.ACTION_CONTENT_ELEMENT_REP:
            case PrologParser.ACTION_CONTENT_ELEMENT_OPT:
            case PrologParser.ACTION_CONTENT_ELEMENT_PLUS:
                declState.contentSpec.append(stringCache.convert(buf,
                            currentTokenStart,
                            bufStart,
                            false));
                break;
            case PrologParser.ACTION_GROUP_CLOSE:
            case PrologParser.ACTION_GROUP_CLOSE_REP:
            case PrologParser.ACTION_GROUP_CLOSE_OPT:
            case PrologParser.ACTION_GROUP_CLOSE_PLUS:
                declState.contentSpec.append(stringCache.convert(buf,
                            currentTokenStart,
                            bufStart,
                            false));
                if (pp.getGroupLevel() == 0)
                    declState.elementType.setContentSpec(declState.contentSpec.toString());
                break;
            case PrologParser.ACTION_DECL_CLOSE:
                if (declState.declType >= 0
                            && declState.declType != MarkupDeclarationEvent.ATTRIBUTE)
                    reportMarkupDeclaration(declState);
                declState.declType = -1;
                break;
        }
    }

    private final void startEntityDecl(Hashtable table, DeclState declState) {
        String name = stringCache.convert(buf, currentTokenStart, bufStart, true);
        declState.entity = (EntityImpl)table.get(name);
        if (declState.entity == null) {
            declState.entity = new EntityImpl();
            table.put(name, declState.entity);
            declState.declName = name;
        }
        else {
            declState.entity = null;
            declState.declType = -1;
        }
    }

    private final void skipIgnoreSect() throws IOException {
        for (;;) {
            try {
                bufStart = enc.skipIgnoreSect(buf, bufStart, bufEnd);
                return;
            }
            catch (PartialTokenException e) {
                if (!fill()) {
                    currentTokenStart = bufStart;
                    fatal(MessageId.UNCLOSED_CONDITIONAL_SECTION);
                }
            }
            catch (InvalidTokenException e) {
                currentTokenStart = e.getOffset();
                fatal(MessageId.IGNORE_SECT_CHAR);
            }
        }
    }
    
    private int nOpenElements = 0;
    byte[] openElementNameBuf = new byte[64];
    int[] openElementNameStart = new int[8];

    public final void parseContent(boolean oneElement, boolean exitIfNoData) throws IOException, ApplicationException {
        byte[] buf = this.buf;
        int bufEnd = this.bufEnd;
        int bufStart = this.bufStart;
        Encoding enc = this.enc;
    //    int nOpenElements = 0;
//        byte[] openElementNameBuf = new byte[64];
        // Indexed by nOpenElements
//        int[] openElementNameStart = new int[8];
//        openElementNameStart[0] = 0;
        for (;;) {
            try {
                switch (enc.tokenizeContent(buf, bufStart, bufEnd, this)) {
                    case Encoding.TOK_START_TAG_WITH_ATTS:
                        storeAtts();
                        /* fall through */
                    case Encoding.TOK_START_TAG_NO_ATTS:
                        if (nOpenElements + 1 >= openElementNameStart.length)
                            openElementNameStart = grow(openElementNameStart);
                        nameStart = bufStart + minBPC;
                        nAttributes = -1;
                        /* Update currentTokenStart so that getLocation works. */
                        currentTokenStart = bufStart;
                        try {
                            app.startElement(this);
                        }
                        catch (RuntimeException e) {
                            throw e;
                        }
                        catch (Exception e) {
                            throw new ApplicationException(e);
                        }
                        int nameLength = getNameEnd() - nameStart;
                        int nameBufEnd = openElementNameStart[nOpenElements];
                        if ((openElementNameStart[nOpenElements + 1]
                                                  = nameBufEnd + nameLength)
                                                  > openElementNameBuf.length) {
                            byte[] tem
                            = new byte[(openElementNameBuf.length << 1) + nameLength];
                            System.arraycopy(openElementNameBuf,
                                        0,
                                        tem,
                                        0,
                                        openElementNameStart[nOpenElements]);
                            openElementNameBuf = tem;
                        }
                        copyBytes(buf, nameStart, openElementNameBuf, nameBufEnd, nameLength);
                        nOpenElements++;
                        break;
                    case Encoding.TOK_EMPTY_ELEMENT_WITH_ATTS:
                        storeAtts();
                        /* fall through */
                    case Encoding.TOK_EMPTY_ELEMENT_NO_ATTS:
                        nameStart = bufStart + minBPC;
                        nAttributes = -1;

                        /* Update currentTokenStart so that getLocation works. */
                        currentTokenStart = bufStart;
                        try {
                            app.startElement(this);
                            app.endElement(this);
                        }
                        catch (RuntimeException e) {
                            throw e;
                        }
                        catch (Exception e) {
                            throw new ApplicationException(e);
                        }
                        if (oneElement && nOpenElements == 0) {
                            this.bufStart = getTokenEnd();
                            return;
                        }
                        break;
                    case Encoding.TOK_END_TAG:
                        if (nOpenElements == 0) {
                            currentTokenStart = bufStart;
                            fatal(MessageId.INVALID_END_TAG);
                        }
                        --nOpenElements;
                        nameStart = bufStart + 2*minBPC;
                        if (!bytesEqual(openElementNameBuf,
                                    openElementNameStart[nOpenElements],
                                    openElementNameStart[nOpenElements + 1],
                                    buf,
                                    nameStart,
                                    getNameEnd())) {
                            String expected
                            = stringCache.convert(openElementNameBuf,
                                        openElementNameStart[nOpenElements],
                                        openElementNameStart[nOpenElements + 1],
                                        false);
                            String got = stringCache.convert(buf,
                                        nameStart,
                                        getNameEnd(),
                                        false);
                            currentTokenStart = bufStart;
                            fatal(MessageId.MISMATCHED_END_TAG, got, expected);
                        }
                        try {
                            app.endElement(this);
                        }
                        catch (RuntimeException e) {
                            throw e;
                        }
                        catch (Exception e) {
                            throw new ApplicationException(e);
                        }
                        if (oneElement && nOpenElements == 0) {
                            this.bufStart = getTokenEnd();
                            return;
                        }
                        break;
                    case Encoding.TOK_DATA_CHARS:
                        data = null;
                        this.bufStart = bufStart;
                        reportCharacterData();
                        break;
                    case Encoding.TOK_DATA_NEWLINE:
                        dataBuf[0] = '\n';
                        dataLength = 1;
                        data = dataBuf;
                        reportCharacterData();
                        break;
                    case Encoding.TOK_MAGIC_ENTITY_REF:
                    case Encoding.TOK_CHAR_REF:
                        dataBuf[0] = getRefChar();
                        dataLength = 1;
                        data = dataBuf;
                        dataIsRef = true;
                        reportCharacterData();
                        dataIsRef = false;
                        break;
                    case Encoding.TOK_CHAR_PAIR_REF:
                        getRefCharPair(dataBuf, 0);
                        data = dataBuf;
                        dataLength = 2;
                        dataIsRef = true;
                        reportCharacterData();
                        dataIsRef = false;
                        break;
                    case Encoding.TOK_CDATA_SECT_OPEN:
                        currentTokenStart = bufStart;
                        try {
                            app.startCdataSection(this);
                        }
                        catch (RuntimeException e) {
                            throw e;
                        }
                        catch (Exception e) {
                            throw new ApplicationException(e);
                        }
                        this.bufStart = getTokenEnd();
                        parseCdataSection();
                        buf = this.buf;
                        bufStart = this.bufStart;
                        bufEnd = this.bufEnd;
                        break;
                    case Encoding.TOK_ENTITY_REF:
                    {
                        nameStart = bufStart + minBPC;
                        String name = stringCache.convert(buf,
                                    nameStart,
                                    getNameEnd(),
                                    true);
                        EntityImpl entity = (EntityImpl)dtd.generalEntityTable.get(name);
                        if (entity == null) {
                            if (dtd.complete || dtd.standalone) {
                                currentTokenStart = bufStart;
                                fatal(MessageId.UNDEF_REF, name);
                            }
                            break;
                        }
                        EntityParser parser = makeParserForEntity(entity, name, false);
                        if (parser == null)
                            break;
                        reportStartEntityReference();
                        entity.open = true;
                        if (entity.text != null) {
                            currentTokenStart = this.bufStart = bufStart;
                            parser.parseContent(false, false);
                        }
                        else
                            parser.parseExternalTextEntity();
                        reportEndEntityReference();
                        entity.open = false;
                        break;
                    }
                    case Encoding.TOK_PI:
                        nameStart = bufStart + minBPC*2;
                        currentTokenStart = bufStart;
                        reportProcessingInstruction();
                        break;
                    case Encoding.TOK_COMMENT:
                        currentTokenStart = bufStart;
                        reportComment();
                        break;
                    case Encoding.TOK_XML_DECL:
                        currentTokenStart = bufStart;
                        fatal(MessageId.MISPLACED_XML_DECL);
                }
                bufStart = getTokenEnd();
            }
            catch (EmptyTokenException e) {
                this.bufStart = bufStart;
                if (!fill()) {
                    if (!exitIfNoData &&  (oneElement || nOpenElements > 0)) {
                        currentTokenStart = this.bufStart;
                        fatal(MessageId.MISSING_END_TAG);
                    }
                    return;
                }
                buf = this.buf;
                bufStart = this.bufStart;
                bufEnd = this.bufEnd;
                if (exitIfNoData)
                    return;
            }
            catch (PartialTokenException e) {
                this.bufStart = bufStart;
                if (!fill()) {
                    currentTokenStart = this.bufStart;
                    fatal(MessageId.UNCLOSED_TOKEN);
                }
                buf = this.buf;
                bufStart = this.bufStart;
                bufEnd = this.bufEnd;
                if (exitIfNoData)
                    return;
                
            }
            catch (ExtensibleTokenException e) {
                this.bufStart = bufStart;
                if (!fill()) {
                    if (oneElement || nOpenElements > 0) {
                        currentTokenStart = this.bufStart;
                        fatal(MessageId.MISSING_END_TAG);
                    }
                    switch (e.getTokenType()) {
                        case Encoding.TOK_DATA_NEWLINE:
                            dataBuf[0] = '\n';
                            dataLength = 1;
                            data = dataBuf;
                            reportCharacterData();
                            break;
                        case Encoding.TOK_DATA_CHARS:
                            data = null;
                            setTokenEnd(this.bufEnd);
                            reportCharacterData();
                            break;
                        default:
                            throw new Error("extensible token botch");
                    }
                    return;
                }
                buf = this.buf;
                bufStart = this.bufStart;
                bufEnd = this.bufEnd;
                
                // tim: 12/20/07 -- I think the below is necessary in case a client sends us 
                // the beginning of an extensible token and then stops sending us anything.
                if (exitIfNoData) 
                    return;
            }
            catch (InvalidTokenException e) {
                currentTokenStart = e.getOffset();
                reportInvalidToken(e);
            }
        }
    }

    private final void parseCdataSection() throws IOException, InvalidTokenException, ApplicationException {
        for (;;) {
            try {
                switch (enc.tokenizeCdataSection(buf, bufStart, bufEnd, this)) {
                    case Encoding.TOK_DATA_CHARS:
                        data = null;
                        reportCharacterData();
                        break;
                    case Encoding.TOK_DATA_NEWLINE:
                        dataBuf[0] = '\n';
                        dataLength = 1;
                        data = dataBuf;
                        reportCharacterData();
                        break;
                    case Encoding.TOK_CDATA_SECT_CLOSE:
                        currentTokenStart = bufStart;
                        try {
                            app.endCdataSection(this);
                        }
                        catch (RuntimeException e) {
                            throw e;
                        }
                        catch (Exception e) {
                            throw new ApplicationException(e);
                        }
                        return;
                }
                bufStart = getTokenEnd();
            }
            catch (InvalidTokenException e) {
                throw e;
            }
            catch (TokenException e) {
                if (!fill()) {
                    currentTokenStart = this.bufStart;
                    fatal(MessageId.UNCLOSED_CDATA_SECTION);
                }
            }
        }
    }

    private EntityParser makeParserForEntity(EntityImpl entity, String name, boolean isParameter) throws IOException {
        if (entity.open)
            fatal(MessageId.RECURSION);
        if (entity.notationName != null)
            fatal(MessageId.UNPARSED_REF);
        if (entity.text != null)
            return new EntityParser(entity.text, name, isParameter, this);
        OpenEntity openEntity
        = entityManager.open(entity.systemId, entity.baseURL, entity.publicId);
        if (openEntity == null)
            return null;
        return new EntityParser(openEntity, entityManager, app, locale, this);
    }

    private final void storeAtts() throws NotWellFormedException {
        int i = getAttributeSpecifiedCount();
        ElementTypeImpl elementType = null;
        boolean gotElementType = false;
        while (i != 0) {
            --i;
            if (!isAttributeNormalized(i)) {
                valueBuf.clear();
                if (!gotElementType)
                    elementType
                    = (ElementTypeImpl)dtd.elementTypeTable
                    .get(stringCache.convert(buf,
                                bufStart + minBPC,
                                getNameEnd(),
                                true));
                boolean isCdata;
                if (elementType != null) {
                    String attName
                    = stringCache.convert(buf,
                                getAttributeNameStart(i),
                                getAttributeNameEnd(i),
                                true);
                    isCdata = elementType.isAttributeCdata(attName);
                }
                else
                    isCdata = true;
                String val = makeAttributeValue(isCdata,
                            buf,
                            getAttributeValueStart(i),
                            getAttributeValueEnd(i));
                setAttributeValue(i, val);
            }
        }
    }

    private String makeAttributeValue(boolean isCdata,
                byte[] buf,
                int start,
                int end) throws NotWellFormedException {

        /* appendAttributeValue will trash currentTokenStart. */
        int saveCurrentTokenStart = currentTokenStart;
        int saveNameEnd = getNameEnd();
        valueBuf.clear();
        appendAttributeValue(isCdata, start, end, valueBuf);
        if (!isCdata
                    && valueBuf.length() > 0
                    && valueBuf.charAt(valueBuf.length() - 1) == ' ')
            valueBuf.chop();
        currentTokenStart = saveCurrentTokenStart;
        setNameEnd(saveNameEnd);
        return valueBuf.toString();
    }

    private void appendAttributeValue(boolean isCdata,
                int start,
                int end,
                Buffer valueBuf) throws NotWellFormedException {
        Token t = new Token();
        try {
            for (;;) {
                int tok;
                int nextStart;
                try {
                    tok = enc.tokenizeAttributeValue(buf, start, end, t);
                    nextStart = t.getTokenEnd();
                }
                catch (ExtensibleTokenException e) {
                    tok = e.getTokenType();
                    nextStart = end;
                }
                currentTokenStart = start;
                switch (tok) {
                    case Encoding.TOK_DATA_CHARS:
                        valueBuf.append(enc, buf, start, t.getTokenEnd());
                        break;
                    case Encoding.TOK_MAGIC_ENTITY_REF:
                    case Encoding.TOK_CHAR_REF:
                        if (isCdata
                                    || t.getRefChar() != ' '
                                        || (valueBuf.length() > 0
                                                    && valueBuf.charAt(valueBuf.length() - 1) != ' '))
                            valueBuf.append(t.getRefChar());
                        break;
                    case Encoding.TOK_CHAR_PAIR_REF:
                        valueBuf.appendRefCharPair(t);
                        break;
                    case Encoding.TOK_ATTRIBUTE_VALUE_S:
                    case Encoding.TOK_DATA_NEWLINE:
                        if (isCdata
                                    || (valueBuf.length() > 0
                                                && valueBuf.charAt(valueBuf.length() - 1) != ' '))
                            valueBuf.append(' ');
                        break;
                    case Encoding.TOK_ENTITY_REF:
                        String name = stringCache.convert(buf,
                                    start + minBPC,
                                    t.getTokenEnd() - minBPC,
                                    true);
                        EntityImpl entity = (EntityImpl)dtd.generalEntityTable.get(name);
                        if (entity == null) {
                            if (dtd.complete || dtd.standalone)
                                fatal(MessageId.UNDEF_REF, name);
                            break;
                        }
                        if (entity.systemId != null)
                            fatal(MessageId.EXTERN_REF_ATTVAL);
                        try {
                            EntityParser parser = makeParserForEntity(entity, name, false);
                            entity.open = true;
                            parser.appendAttributeValue(isCdata, 0, parser.bufEnd, valueBuf);
                            entity.open = false;
                        }
                        catch (NotWellFormedException e) {
                            throw e;
                        }
                        catch (IOException e) {
                            // Shouldn't happen since the entity is internal.
                            throw new Error("unexpected IOException");
                        }
                        break;
                    default:
                        throw new Error("attribute value botch");
                }
                start = nextStart;
            }
        }
        catch (PartialTokenException e) {
            currentTokenStart = end;
            fatal(MessageId.NOT_WELL_FORMED);
        }
        catch (InvalidTokenException e) {
            currentTokenStart = e.getOffset();
            reportInvalidToken(e);
        }
        catch (EmptyTokenException e) { }
    }

    /*
     * Make the replacement text for an entity out of the literal in the
     * current token.
     */
    private byte[] makeReplacementText(boolean allowPerefs) throws IOException {
        valueBuf.clear();
        Token t = new Token();
        int start = currentTokenStart + minBPC;
        final int end = bufStart - minBPC;
        try {
            for (;;) {
                int tok;
                int nextStart;
                try {
                    tok = enc.tokenizeEntityValue(buf, start, end, t);
                    nextStart = t.getTokenEnd();
                }
                catch (ExtensibleTokenException e) {
                    tok = e.getTokenType();
                    nextStart = end;
                }
                if (tok == Encoding.TOK_PARAM_ENTITY_REF && !allowPerefs) {
                    currentTokenStart = start;
                    fatal(MessageId.INTERNAL_PEREF_ENTVAL);
                }
                handleEntityValueToken(valueBuf, tok, start, nextStart, t);
                start = nextStart;
            }
        }
        catch (PartialTokenException e) {
            currentTokenStart = end;
            fatal(MessageId.NOT_WELL_FORMED);
        }
        catch (InvalidTokenException e) {
            currentTokenStart = e.getOffset();
            reportInvalidToken(e);
        }
        catch (EmptyTokenException e) { }

        return valueBuf.getBytes();
    }

    private void parseEntityValue(Buffer value) throws IOException {
        final Token t = new Token();
        for (;;) {
            int tok;
            for (;;) {
                try {
                    tok = enc.tokenizeEntityValue(buf, bufStart, bufEnd, t);
                    currentTokenStart = bufStart;
                    bufStart = t.getTokenEnd();
                    break;
                }
                catch (EmptyTokenException e) {
                    if (!fill())
                        return;
                }
                catch (PartialTokenException e) {
                    if (!fill()) {
                        currentTokenStart = bufStart;
                        bufStart = bufEnd;
                        fatal(MessageId.UNCLOSED_TOKEN);
                    }
                }
                catch (ExtensibleTokenException e) {
                    if (!fill()) {
                        currentTokenStart = bufStart;
                        bufStart = bufEnd;
                        tok = e.getTokenType();
                        break;
                    }
                }
                catch (InvalidTokenException e) {
                    currentTokenStart = e.getOffset();
                    reportInvalidToken(e);
                }
            }
            handleEntityValueToken(value, tok, currentTokenStart, bufStart, t);
        }
    }

    private void handleEntityValueToken(Buffer value, int tok, int start, int end, Token t) throws IOException {
        switch (tok) {
            case Encoding.TOK_DATA_CHARS:
            case Encoding.TOK_ENTITY_REF:
            case Encoding.TOK_MAGIC_ENTITY_REF:
                value.append(enc, buf, start, end);
                break;
            case Encoding.TOK_CHAR_REF:
                value.append(t.getRefChar());
                break;
            case Encoding.TOK_CHAR_PAIR_REF:
                value.appendRefCharPair(t);
                break;
            case Encoding.TOK_DATA_NEWLINE:
                value.append('\n');
                break;
            case Encoding.TOK_PARAM_ENTITY_REF:
                String name = stringCache.convert(buf,
                            start + minBPC,
                            end - minBPC,
                            true);
                EntityImpl entity = (EntityImpl)dtd.paramEntityTable.get(name);
                if (entity == null) {
                    if (dtd.complete)
                        fatal(MessageId.UNDEF_PEREF, name);
                    break;
                }
                EntityParser parser = makeParserForEntity(entity, name, true);
                if (parser != null) {
                    entity.open = true;
                    parser.parseEntityValue(value);
                    entity.open = false;
                }
                break;
            default:
                throw new Error("replacement text botch");
        }
    }

    private void parseMisc() throws IOException, ApplicationException {
        try {
            for (;;) {
                switch (tokenizeProlog()) {
                    case Encoding.TOK_PI:
                        nameStart = currentTokenStart + minBPC + minBPC;
                        reportProcessingInstruction();
                        break;
                    case Encoding.TOK_COMMENT:
                        reportComment();
                        break;
                    case Encoding.TOK_PROLOG_S:
                        break;
                    default:
                        fatal(MessageId.EPILOG_JUNK);
                }
            }
        }
        catch (EndOfPrologException e) {
            currentTokenStart = bufStart;
            fatal(MessageId.ELEMENT_AFTER_DOCUMENT_ELEMENT);
        }
        catch (EmptyTokenException e) { }
    }

    private final int tokenizeProlog()
    throws IOException, EmptyTokenException, EndOfPrologException {
        for (;;) {
            try {
                int tok = enc.tokenizeProlog(buf, bufStart, bufEnd, this);
                currentTokenStart = bufStart;
                bufStart = getTokenEnd();
                return tok;
            }
            catch (EmptyTokenException e) {
                if (!fill())
                    throw e;
            }
            catch (PartialTokenException e) {
                if (!fill()) {
                    currentTokenStart = bufStart;
                    bufStart = bufEnd;
                    fatal(MessageId.UNCLOSED_TOKEN);
                }
            }
            catch (ExtensibleTokenException e) {
                if (!fill()) {
                    currentTokenStart = bufStart;
                    bufStart = bufEnd;
                    return e.getTokenType();
                }
            }
            catch (InvalidTokenException e) {
                bufStart = currentTokenStart = e.getOffset();
                reportInvalidToken(e);
            }
        }
    }

    private static final int[] grow(int[] v) {
        int[] tem = v;
        v = new int[tem.length << 1];
        System.arraycopy(tem, 0, v, 0, tem.length);
        return v;
    }

    private long getEntityByteIndex(int off) {
        return bufEndStreamOffset - (bufEnd - off);
    }

//    /* The size of the buffer is always a multiple of READSIZE.
//     We do reads so that a complete read would end at the
//     end of the buffer.  Unless there has been an incomplete
//     read, we always read in multiples of READSIZE. */
//    private boolean fill() throws IOException {
//        if (in == null)
//            return false;
//        if (bufEnd == buf.length) {
//            enc.movePosition(buf, posOff, bufStart, pos);
//            /* The last read was complete. */
//            int keep = bufEnd - bufStart;
//            if (keep == 0)
//                bufEnd = 0;
//            else if (keep + READSIZE <= buf.length) {
//                /*
//                 * There is space in the buffer for at least READSIZE bytes.
//                 * Choose bufEnd so that it is the least non-negative integer
//                 * greater than or equal to <code>keep</code>, such
//                 * <code>bufLength - keep</code> is a multiple of READSIZE.
//                 */
//                bufEnd = buf.length - (((buf.length - keep)/READSIZE) * READSIZE);
//                for (int i = 0; i < keep; i++)
//                    buf[bufEnd - keep + i] = buf[bufStart + i];
//            }
//            else {
//                byte newBuf[] = new byte[buf.length << 1];
//                bufEnd = buf.length;
//                System.arraycopy(buf, bufStart, newBuf, bufEnd - keep, keep);
//                buf = newBuf;
//            }
//            bufStart = bufEnd - keep;
//            posOff = bufStart;
//        }
//        int nBytes = in.read(buf, bufEnd, buf.length - bufEnd);
//        if (nBytes < 0) {
//            in.close();
//            in = null;
//            return false;
//        }
//        bufEnd += nBytes;
//        bufEndStreamOffset += nBytes;
//        return true;
//    }
    
    private boolean fill() throws IOException {
        if (mAtEof)
            return false;
        else 
            return true;
    }
    
    private boolean mAtEof = false;
    
    public void setEof() {
        mAtEof = true;
    }
    
    public void addBytes(byte[] _buf, int addLen) {
        assert(addLen <= _buf.length);
        
        if (addLen  == 0)
            return;
        
        int keep = bufEnd - bufStart;
        if (keep == 0) {
            enc.movePosition(buf, posOff, bufStart, pos);
            posOff = 0; 
            buf = _buf;
            bufStart = 0;
            bufEnd = addLen;
            bufEndStreamOffset += addLen;
//            System.out.println("\nBufLength(0)="+buf.length+" added=\""+addedStr+"\"");
        } else {
            if (bufStart > addLen) {
                // re-use current buffer
                enc.movePosition(buf, posOff, bufStart, pos);
                posOff = 0;
                System.arraycopy(buf, bufStart, buf, 0, keep);
                System.arraycopy(_buf, 0, buf, keep, addLen);
                bufStart = 0;
                bufEnd = keep+addLen;
                bufEndStreamOffset += addLen;
//                System.out.println("\nBufLength(2)="+buf.length+" added=\""+addedStr+"\"");
            } else {
                // enlarge buffer
                enc.movePosition(buf, posOff, bufStart, pos);
                posOff = 0; 
                byte[] newBuf = new byte[keep + addLen];
                System.arraycopy(buf, bufStart, newBuf, 0, keep);
                System.arraycopy(_buf, 0, newBuf, keep, addLen);
                bufStart = 0;
                bufEnd = keep+addLen;
                bufEndStreamOffset += addLen;
                buf = newBuf;
//                System.out.println("\nBufLength(1)="+buf.length+" added=\""+addedStr+"\"");
            }
                
            
        }
    }

    private void reportInvalidToken(InvalidTokenException e) throws NotWellFormedException {
        switch (e.getType()) {
            case InvalidTokenException.DUPLICATE_ATTRIBUTE:
                fatal(MessageId.DUPLICATE_ATTRIBUTE);
            case InvalidTokenException.XML_TARGET:
                fatal(MessageId.XML_TARGET);
        }
        fatal(MessageId.ILLEGAL_CHAR);
    }

    private void fatal(String message) throws NotWellFormedException {
        doFatal(message, null);
    }

    private void fatal(String message, Object arg) throws NotWellFormedException {
        doFatal(message, new Object[] { arg });
    }

    private void fatal(String message, Object arg1, Object arg2) throws NotWellFormedException {
        doFatal(message, new Object[] { arg1, arg2 });
    }

    private void doFatal(String id, Object[] args) throws NotWellFormedException {
        if (parent != null)
            parent.doFatal(id, args);
        if (posOff > currentTokenStart)
            throw new Error("positioning botch");
        if (enc != null)
            enc.movePosition(buf, posOff, currentTokenStart, pos);
        posOff = currentTokenStart;
        String desc = id;
        String message = null;
        try {
            ResourceBundle resources
            = ResourceBundle.getBundle("com.jclark.xml.parse.Messages", locale);
            desc = resources.getString(id);
            if (args != null)
                desc = MessageFormat.format(desc, args);
            Object[] msgArgs = new Object[] { desc,
                        location,
                        new Integer(pos.getLineNumber()),
                        new Integer(pos.getColumnNumber()),
                        new Long(getEntityByteIndex(currentTokenStart))
            };
            message = MessageFormat.format(resources
                        .getString(MessageId.MESSAGE_FORMAT),
                        msgArgs);
        }
        catch (MissingResourceException e) { 
            message = desc;
        }
        catch (IllegalArgumentException e) {
            message = desc;
        }
        throw new NotWellFormedException(message,
                    desc,
                    location,
                    baseURL,
                    pos.getLineNumber(),
                    pos.getColumnNumber(),
                    getEntityByteIndex(currentTokenStart));
    }

    private static final
    boolean bytesEqual(byte[] buf1, int start1, int end1,
                byte[] buf2, int start2, int end2) {
        int len = end1 - start1;
        if (end2 - start2 != len)
            return false;
        for (; len > 0; --len)
            if (buf1[start1++] != buf2[start2++])
                return false;
        return true;
    }

    private final static
    void copyBytes(byte[] from, int fromOff, byte[] to, int toOff, int len) {
        while (--len >= 0) {
            to[toOff++] = from[fromOff++];
        }
    }

    public ParseLocation getLocation() {
        if (parent != null)
            return parent.getLocation();
        if (posOff > currentTokenStart)
            throw new Error("positioning botch");
        if (enc != null)
            enc.movePosition(buf, posOff, currentTokenStart, pos);
        posOff = currentTokenStart;
        return this;
    }

    public String getEntityLocation() {
        return location;
    }

    public URL getEntityBase() {
        return baseURL;
    }

    public long getByteIndex() {
        return getEntityByteIndex(currentTokenStart);
    }

    public int getLineNumber() {
        return pos.getLineNumber();
    }

    public int getColumnNumber() {
        return pos.getColumnNumber();
    }

    public String getName() {
        return stringCache.convert(buf, nameStart, getNameEnd(), true);
    }

    public DTD getDTD() {
        return dtd;
    }

    public int getLength() {
        if (data == null) {
            if (fixBPC != 0)
                return (getTokenEnd() - bufStart)/fixBPC;
            convertData(bufStart, getTokenEnd());
        }
        return dataLength;
    }

    public boolean isReference() {
        return dataIsRef;
    }

    public int getLengthMax() {
        if (data != null)
            return dataLength;
        else
            return (getTokenEnd() - bufStart)/minBPC;
    }

    public int copyChars(char[] cbuf, int off) {
        if (data != null) {
            System.arraycopy(data, 0, cbuf, off, dataLength);
            return dataLength;
        }
        else
            return enc.convert(buf, bufStart, getTokenEnd(), cbuf, off);
    }

    public void writeChars(Writer writer) throws IOException {
        if (data == null)
            convertData(bufStart, getTokenEnd());
        writer.write(data, 0, dataLength);
    }

    private void convertData(int start, int end) {
        if (dataBuf == null || dataBuf.length * minBPC < end - start)
            dataBuf = new char[(end - start)/minBPC];
        dataLength = enc.convert(buf, start, end, dataBuf, 0);
        data = dataBuf;
    }

    private final void setAttributeValue(int index, String value) {
        if (attValues == null)
            attValues = new String[index + 10];
        else if (index >= attValues.length) {
            String[] tem = new String[index << 1];
            System.arraycopy(attValues, 0, tem, 0, attValues.length);
            attValues = tem;
        }
        attValues[index] = value;
    }

    public final int getAttributeCount() {
        if (nAttributes < 0)
            buildAttributes();
        return nAttributes;
    }

    public final int getIdAttributeIndex() {
        if (nAttributes < 0)
            buildAttributes();
        return idAttributeIndex;
    }

    public final String getAttributeName(int i) {
        if (nAttributes < 0)
            buildAttributes();
        if (i >= nAttributes)
            throw new IndexOutOfBoundsException();
        return attNames[i];
    }

    public final String getAttributeValue(int i) {
        if (nAttributes < 0)
            buildAttributes();
        if (i < getAttributeSpecifiedCount()) {
            if (isAttributeNormalized(i))
                return stringCache.convert(buf,
                            getAttributeValueStart(i),
                            getAttributeValueEnd(i),
                            false);
        }
        else if (i >= nAttributes)
            throw new IndexOutOfBoundsException();
        return attValues[i];
    }

    public final String getAttributeUnnormalizedValue(int i) {
        if (i >= getAttributeSpecifiedCount() || i < 0)
            throw new IndexOutOfBoundsException();
        return normalizeNewlines(stringCache.convert(buf,
                    getAttributeValueStart(i),
                    getAttributeValueEnd(i),
                    false));
    }

    public final String getAttributeValue(String name) {
        if (nAttributes < 0)
            buildAttributes();
        for (int i = 0; i < nAttributes; i++) {
            if (attNames[i].equals(name)) {
                if (i < getAttributeSpecifiedCount() && isAttributeNormalized(i))
                    return stringCache.convert(buf,
                                getAttributeValueStart(i),
                                getAttributeValueEnd(i),
                                false);
                else
                    return attValues[i];
            }
        }
        return null;
    }

    private void buildAttributes() {
        ElementTypeImpl elementType
        = (ElementTypeImpl)dtd.elementTypeTable.get(getName());
        int nSpecAtts = getAttributeSpecifiedCount();

        {
            int totalAtts = nSpecAtts;
            if (elementType != null)
                totalAtts += elementType.getDefaultAttributeCount();
            if (attNames == null || totalAtts > attNames.length)
                attNames = new String[totalAtts];
        }
        for (int i = nSpecAtts; --i >= 0;)
            attNames[i] = stringCache.convert(buf,
                        getAttributeNameStart(i),
                        getAttributeNameEnd(i),
                        true);
        nAttributes = nSpecAtts;
        idAttributeIndex = -1;
        if (elementType != null) {
            int nDefaults = elementType.getDefaultAttributeCount();
            if (defaultSpecified == null
                        || nDefaults > defaultSpecified.length)
                defaultSpecified = new boolean[nDefaults];
            else {
                for (int i = 0; i < nDefaults; i++)
                    defaultSpecified[i] = false;
            }
            for (int i = nSpecAtts; --i >= 0;) {
                int di = elementType.getAttributeDefaultIndex(attNames[i]);
                if (di >= 0)
                    defaultSpecified[di] = true;
                else if (di == ElementTypeImpl.ID_DEFAULT_INDEX)
                    idAttributeIndex = i;
            }
            for (int i = 0; i < nDefaults; i++) {
                if (!defaultSpecified[i]) {
                    setAttributeValue(nAttributes,
                                elementType.getDefaultAttributeValue(i));
                    attNames[nAttributes] = elementType.getDefaultAttributeName(i);
                    ++nAttributes;
                }
            }
        }
    }

    public final String getComment() {
        return normalizeNewlines(stringCache.convert(buf,
                    currentTokenStart + 4*minBPC,
                    getTokenEnd() - 3*minBPC,
                    false));
    }

    public final String getInstruction() {
        return normalizeNewlines(stringCache.convert(buf,
                    enc.skipS(buf,
                                getNameEnd(),
                                getTokenEnd()),
                                getTokenEnd() - 2*minBPC,
                                false));
    }

    private final String normalizeNewlines(String str) {
        int i = str.indexOf('\r');
        if (i < 0)
            return str;
        StringBuffer buf = new StringBuffer();
        for (i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\r') {
                buf.append('\n');
                if (i + 1 < str.length() && str.charAt(i + 1) == '\n')
                    i++;
            }
            else
                buf.append(c);
        }
        return buf.toString();
    }

    private final void reportCharacterData() throws ApplicationException {
        try {
            app.characterData(this);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private final void reportProcessingInstruction() throws ApplicationException {
        try {
            app.processingInstruction(this);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private final void reportComment() throws ApplicationException {
        try {
            app.comment(this);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private final void reportStartEntityReference() throws ApplicationException {
        try {
            app.startEntityReference(this);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private final void reportEndEntityReference() throws ApplicationException {
        try {
            app.endEntityReference(this);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private final void reportMarkupDeclaration(DeclState declState) throws ApplicationException {
        try {
            app.markupDeclaration(declState);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private final void reportStartDocumentTypeDeclaration(DeclState declState) throws ApplicationException {
        try {
            app.startDocumentTypeDeclaration(declState);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private final void reportEndDocumentTypeDeclaration(DeclState declState) throws ApplicationException {
        try {
            app.endDocumentTypeDeclaration(declState);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
}
