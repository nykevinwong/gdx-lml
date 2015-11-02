package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.Lml;

/** Abstract base for logging macros. Logs passed attributes and content.
 *
 * @author MJ */
public abstract class AbstractLoggerLmlMacroTag extends AbstractMacroLmlTag {
    private String content;

    public AbstractLoggerLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final String rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final String rawData) {
        content = replaceArguments(rawData, getParser().getData().getArguments());
    }

    @Override
    public void closeTag() {
        if (!isOn()) {
            return;
        }
        final Array<String> attributes = getAttributes();
        if (GdxArrays.isEmpty(attributes)) {
            if (Strings.isNotEmpty(content)) {
                // Only content between tags is given:
                log(Lml.LOGGER_TAG, content);
            }
        } else {
            if (Strings.isNotBlank(content)) {
                attributes.add(content);
            }
            log(Lml.LOGGER_TAG, Strings.join(" ", attributes));
        }
    }

    /** @return true if this logger is currently turned on. */
    protected abstract boolean isOn();

    /** @param loggerTag logging message tag. Usually used to indicate which application's component is problematic.
     * @param message will be logged. */
    protected abstract void log(String loggerTag, String message);
}
