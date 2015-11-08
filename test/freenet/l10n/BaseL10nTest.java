package freenet.l10n;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import freenet.l10n.BaseL10n.LANGUAGE;
import freenet.support.HTMLNode;
import freenet.support.Logger;
import freenet.support.Logger.LogLevel;
import freenet.support.SimpleFieldSet;
import junit.framework.TestCase;

public class BaseL10nTest extends TestCase {
    private static final String L10N_PATH = "freenet/l10n";
    private static final String TEST_PATH = "../test";
    
    public void testAddL10nSubstitution() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode boldNode = new HTMLNode("b");
        l10n.addL10nSubstitution(node, "test.substitution",
                new String[] {"bold"},
                new HTMLNode[] {boldNode});
        assertEquals("Text with <b>loud</b> string", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionExtra() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode boldNode = new HTMLNode("b");
        HTMLNode extraNode = new HTMLNode("extra");
        l10n.addL10nSubstitution(node, "test.substitution",
                new String[] {"bold", "extra"},
                new HTMLNode[] {boldNode, extraNode});
        assertEquals("Text with <b>loud</b> string", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionUnclosed() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode imgNode = new HTMLNode("img");
        l10n.addL10nSubstitution(node, "test.unclosedSubstitution",
                new String[] {"image"},
                new HTMLNode[] {imgNode});
        assertEquals("Text with <img /> unclosed substitution", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionUnclosedMissing() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        l10n.addL10nSubstitution(node, "test.unclosedSubstitution",
                new String[] {},
                new HTMLNode[] {});
        assertEquals("Text with  unclosed substitution", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionMultiple() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode rep1Node = new HTMLNode("r1");
        HTMLNode rep2Node = new HTMLNode("r2");
        HTMLNode rep3Node = new HTMLNode("r3");
        l10n.addL10nSubstitution(node, "test.multipleSubstitution",
                new String[] {"rep2", "rep1", "rep3"},
                new HTMLNode[] {rep2Node, rep1Node, rep3Node});
        assertEquals("<r1>Rep 1</r1><r2>Rep 2</r2> and <r3>Rep 3</r3>", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionMissing() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode rep2Node = new HTMLNode("r2");
        HTMLNode rep3Node = new HTMLNode("r3");
        l10n.addL10nSubstitution(node, "test.multipleSubstitution",
                new String[] {"rep2", "rep3"},
                new HTMLNode[] {rep2Node, rep3Node});
        assertEquals("Rep 1<r2>Rep 2</r2> and <r3>Rep 3</r3>", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionNested() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode innerNode = new HTMLNode("in");
        HTMLNode outerNode = new HTMLNode("out");
        l10n.addL10nSubstitution(node, "test.nestedSubstitution",
                new String[] {"inner", "outer"},
                new HTMLNode[] {innerNode, outerNode});
        assertEquals("<out>Text and <in>replacement</in></out>", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionDouble() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode tagNode = new HTMLNode("tag");
        l10n.addL10nSubstitution(node, "test.doubleSubstitution",
                new String[] {"tag"},
                new HTMLNode[] {tagNode});
        assertEquals("<tag></tag>content<tag></tag>", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionSelfNested() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode tagNode = new HTMLNode("tag");
        l10n.addL10nSubstitution(node, "test.selfNestedSubstitution",
                new String[] {"tag"},
                new HTMLNode[] {tagNode});
        // it would be nice to handle this correctly, but it seems like more trouble than it's worth
        //assertEquals("<tag>content <tag>nested</tag></tag>", node.generateChildren());
        assertEquals("", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionSelfNestedEmpty() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode tagNode = new HTMLNode("tag");
        l10n.addL10nSubstitution(node, "test.emptySelfNestedSubstitution",
                new String[] {"tag"},
                new HTMLNode[] {tagNode});
        assertEquals("<tag>content <tag></tag>nested</tag>", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionMissingBrace() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode okNode = new HTMLNode("ok");
        l10n.addL10nSubstitution(node, "test.missingBraceSubstitution",
                new String[] {"ok"},
                new HTMLNode[] {okNode});
        assertEquals("", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionUnmatchedClose() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        HTMLNode node = new HTMLNode("div");
        HTMLNode okNode = new HTMLNode("ok");
        l10n.addL10nSubstitution(node, "test.unmatchedCloseSubstitution",
                new String[] {"ok"},
                new HTMLNode[] {okNode});
        assertEquals("", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionFallback() {
        BaseL10n l10n = createTestL10n(LANGUAGE.GERMAN);
        HTMLNode node = new HTMLNode("div");
        HTMLNode tagNode = new HTMLNode("tag");
        l10n.addL10nSubstitution(node, "test.badSubstitutionFallback",
                new String[] {"tag"},
                new HTMLNode[] {tagNode});
        assertEquals("Fallback <tag></tag>", node.generateChildren());
    }
    
    public void testAddL10nSubstitutionMissingFallback() {
        BaseL10n l10n = createTestL10n(LANGUAGE.GERMAN);
        HTMLNode node = new HTMLNode("div");
        HTMLNode boldNode = new HTMLNode("b");
        l10n.addL10nSubstitution(node, "test.substitution",
                new String[] {"bold"},
                new HTMLNode[] {boldNode});
        assertEquals("Text with <b>loud</b> string", node.generateChildren());
    }
    
    public void testGetString() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        String value = l10n.getString("test.sanity");
        assertEquals("Sane", value);
    }

    public void testGetStringOverridden() {
        BaseL10n l10n = createTestL10n(LANGUAGE.ENGLISH);
        String value = l10n.getString("test.override");
        assertEquals("Overridden", value);
    }

    public void testGetStringFallback() {
        BaseL10n l10n = createTestL10n(LANGUAGE.GERMAN);
        String value = l10n.getString("test.sanity");
        assertEquals("Sane", value);
    }

    public void testGetStringFallbackOverridden() {
        BaseL10n l10n = createTestL10n(LANGUAGE.GERMAN);
        String value = l10n.getString("test.override");
        assertEquals("Not overridden", value);
    }

    public void testGetStringNonexistent() {
        BaseL10n l10n = createTestL10n(LANGUAGE.GERMAN);
        String value = l10n.getString("test.nonexistent");
        assertEquals("test.nonexistent", value);
    }
    
    public void testStrings() throws Exception {
        Logger.setupStdoutLogging(LogLevel.ERROR, null);
        for (LANGUAGE lang : LANGUAGE.values()) {
            BaseL10n l10n = createL10n(lang);
            SimpleFieldSet fields = l10n.getCurrentLanguageTranslation();
            if (fields != null) {
                for (Iterator<String> itr = fields.keyIterator(); itr.hasNext();) {
                    String key = itr.next();
                    String value = fields.get(key);
                    boolean success = l10n.isL10nValid(key, value);
                    assertTrue("Error in "+key+" for "+lang, success);
                }
            }
        }
    }
    
    private BaseL10n createL10n(LANGUAGE lang) {
        File overrideFile = new File(L10N_PATH, "freenet.l10n.${lang}.override.properties");
        return new BaseL10n(L10N_PATH, "freenet.l10n.${lang}.properties",
                overrideFile.getPath(), lang);
    }
    
    private BaseL10n createTestL10n(LANGUAGE lang) {
        String testL10nPath = new File(TEST_PATH, L10N_PATH).getPath();
        URL classLoaderUrl = getClass().getClassLoader().getResource(".");
        File classLoaderDir = new File(classLoaderUrl.getPath());
        File overrideFile = new File(new File(classLoaderDir, testL10nPath),
                "freenet.l10n.${lang}.override.properties");
        return new BaseL10n(testL10nPath, "freenet.l10n.${lang}.properties",
                overrideFile.getPath(), lang);
    }
}
