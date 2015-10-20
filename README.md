#LibGDX Markup Language
Templates for LibGDX Scene2D with HTML-like syntax and FreeMarker-inspired macros.

##Examples
See [gdx-lml-tests](http://github.com/czyzby/gdx-lml-tests) for example uses of all available tags and macros.

##Maven artifact
To import LML with Gradle, add this dependency to your core:
```
    compile "com.github.czyzby:gdx-lml:1.1.$gdxVersion"
```
Currently supported LibGDX version is **1.7.0**.

If you want to use LML with GWT, you have to add this module to your GdxDefinition:
```
	<inherits name='com.github.czyzby.lml.GdxLml' />
```

##Documentation
See [LibGDX forum thread](http://www.badlogicgames.com/forum/viewtopic.php?f=17&t=18843), [example project](http://github.com/czyzby/gdx-lml-tests), [tutorial](https://github.com/czyzby/gdx-lml/wiki/Tutorial) and [old syntax page (work in progress)](https://github.com/czyzby/gdx-lml/wiki/Syntax).

## What's new
1.0 -> 1.1:

- Dialog parents now add plain text to content table rather than themselves, which matches LML 0 behavior.
- `oneColumn` attribute added to tables. If a table has `oneColumn=true` attribute, its main table will append row after each child. If the table is a dialog, its content table will append rows after each child.
- `appendActorsToStage(Stage, Array<Actor>)` moved from `AbstractLmlParser` to `LmlUtilities`. Now you can easily append your actors result to a stage, honoring `StageAttacher` settings - even if you do not use to default `fillStage` or `createView` methods of `LmlParser`.
- LML keeps some meta-data about the widgets using `Actor#setUserObject(Object)` method. Some of it might still be useful after LML parsing, like stage attachers (they specify how actors are added to the `Stage`). Still, you might want to remove the unnecessary user objects with little effort: and now that's possible using `LmlUtilities#clearLmlUserObjects(Iterable<Actor>)` and `LmlUtilities#clearLmlUserObject(Actor)`, both of which offer removing user objects of the passed actors and their children, if they happen to be actor groups. As a rule of thumb, if you have added your actors to the stage (or used one of automatic stage-filling parser methods) and don't plan to remove&add them again, it is safe to clear LML meta-data.
- Fixed a bug where default horizontal and vertical styles were inverted.

0 -> 1:

This is the biggest update to LML yet. Actually, the whole parsing mechanism was rewritten from scratch (sic!), as it contained a lot of quick, dirty, uncommented code and wasn't very user-friendly when it came to expanding or modifying. Learning on my mistakes, the new LML is parsed much faster, allows to customize a lot more settings (including the syntax itself, which previously contained a lot of static final values) and makes creating custom attributes or tags from less painful to completely painless. If you used LML before, your templates will most likely break after this update, but the fixes are usually easy enough to endure. Core syntax and API changes:

- LML arguments changed from `${dollarSignProceeded}` to `{simplifiedSyntax}`. There was no need for the sign, really, except maybe being a little more similar to FreeMarker (if I remember correctly, this is were I originally got it from). Eventually, I realized that: A) `{}` are never used in templates in any other way anyway, no need for an extra marker, B) this is an extra character that could be easily avoided, after all (AND WE ALL CARE FOR THAT EXTRA BYTE WE SAVE), C) it is often proceeded with a bundle or preference marker, resulting in `#${weirdSyntax}`, that is `@${moreMysterious}` as opposed to `#{that}` or `@{this}`.
- Method invocation operator changed from '&' to '$'. It was misleading and difficult to parse in equations/conditions (as there is also the '&' bit operator). Since actions are used MOSTLY in attributes that expect methods (onClick, onChange and so on) and these do not and did not require the operator to be present, this shouldn't break most of old templates.
- Range separator changed from `minus[0-4]` to `comma[0,2]`. Minus doesn't really allow for proper parsing of negative ranges without some extra effort that could be easily avoided (not to mention that even parsed correctly, they still looked strange), and it was certainly one of my design mistakes. Note that ranges CANNOT USE any of the reserved array or range characters prior to the actual range choosing to work properly. For example, `this,mightGetParsedIncorrectly[0,4]`. As a rule of thumb, you should use only letters and numbers for IDs. 
- Attributes assignment got unified. If, for example, an iteration macro used to assign elements with `:`, now it will use `=`, as any other tag attribute. For example, instead of `<@forEach element:range[0-2]>`, now you will write `<@forEach element=range[0,2]>`.
- The biggest change of all: now you can MODIFY the syntax. Well, not the structure at its core (as in: XML-like structure stays), but you can change pretty much ANY syntax character by implementing `LmlSyntax` or overriding `DefaultLmlSyntax` and choosing your own characters. Want to use different parenthesis or want some old "feature" back? Sure, why not. I decided to use characters instead of strings for simplified parsing and smaller template files.
- Pretty much all values are now ignoring case, starting from tags and attributes (as it used to be), ending with LML argument names (`{argument}` maps to the same value as `{Argument}`) and actor IDs (`id=this` is the same as `id=THIS`), which are the new standards. It is only natural to use completely different IDs for all actors and to keep your arguments not confusing, but if you did use IDs that vary only with their case, you might want to refactor your templates. Just to be clear: both `<LABEL>` and `<label>` tags will be properly parsed as Labels, but if you try to combine different cases for one widget like this: `<label></LABEL>`, this will throw an exception (if the parser is strict).
- Attributes are now registered globally, rather than attached to each tag parser. This means that now attributes can validated by a strict parser and throw exceptions for typos - no more unknown, invalid, unnoticed tag attributes.
- Package changes. Since almost the whole thing was rewritten, the few classes that remained nearly unchanged might have been moved from their original packages in the process. `@ViewAction` was renamed to `@LmlAction`, since I don't want to mix View-related annotations (which should be clearly in my other project, Autumn MVC) with pure LML-related stuff. The name was sort of confusing anyway, and now its both (arguably) easier to find with code assist and one letter shorter (YEAH! YET ANOTHER BYTE SAVED TODAY).
- Completely changed tag and attribute parsers. If you had custom ones, they will not work anymore. The tag and attribute structure is much more pleasant to expand and manage, though, so I'm pretty sure it won't be very hard to update your custom classes.
- `LmlParser` API was simply too big. Instead of just parsing templates, it had to manage a lot of extra data - like skin, i18n bundles, preferences, actions, etc. Some of its old functionalities are moved into several other interfaces (with their default implementations): `LmlData` manages arguments, skins, bundles and so on, `LmlSyntax` contains parsers of attributes, macros and tags and `LmlTemplateReader` takes care of efficient reading of parsed templates character by character.
- `<row>` tag removed, as tags are supposed to create unique actors. `row=true` attribute is usually more convenient, but since row tag could be used to set row defaults, `<@row>` macro was added to support this functionality. Also, `<@column>` macro as added to support column defaults setting. See `TableColumnLmlMacroTag` and `TableRowLmlMacroTag` docs.
- Missing action exceptions are now more meaningful and usually contain both missing action ID and the argument that the action was supposed to consume.

LML 1.0 brings:

- JavaDocs, JavaDocs, JavaDocs. In previous versions, only `Lml` class had proper comments - now every class and every public and protected method has a hopefully meaningful comment. To be honest and not-so-modest, I think that going through comments of `LmlSyntax` and source of `DefaultLmlSyntax`, for example, is a great way to start with LML right now. Every actor, macro and attribute parser now describes what it parses, what it is prepared to parse and how to use it. Most of the classes that might need extension from time to time (for example: adding a new button tag attribute) are much easier to expand in LML 1.0. I've also tried to include some LML syntax examples (tag and macro usages, etc.) where I thought it might be useful. Seriously, go through the JavaDocs.
- Refactored and faster parsing: template parsing used to be... heavy (converting-string-to-char-array-and-then-into-linked-list-of-characters and using-a-lot-of-regexes-to-code-this-faster kind of heavy), but now a lot of more lightweight methods are used. Even before it took a fraction of second on modern CPUs to parse, but still the new version should be noticeably faster, especially on mobile devices. Cheap splits by a single character, efficient macro arguments' replacements and sensible iteration over strings' characters were implemented to make template parsing efficient.
- Full LibGDX tooltips support, including using custom tooltip managers. Tooltip tag attribute is removed, but tooltip tag works just like before: its basically a Table wrapped with LibGDX Tooltip API.
- Actor injection without Autumn MVC! Now you can annotate your fields with `@LmlActor("id")` and use one of parser's `createView` methods to fill your view with parsed actors. You can also attach a listener that will constantly update one of view's fields according to an actor state with `@OnChange("actorId")` (this will work only on widgets that get sensible change events, like check boxes or sliders). To fill view's stage, implement optional `LmlView` interface and watch as the parser does all your widget management work for you.
- Nested comments. You can optionally turn on nested comments setting to enjoy comments that can span over multiple other comments. However, this sacrifices the freedom of keeping broken comment tags inside your comments: to exit the comment properly, the nested comments HAVE to be valid. This does not match HTML behavior. 
- `@comment` macro. Basically, this macro ignores whatever it has between its tags. This is an alternative (and the preferred) way to use nested comments that might span over multiple lines and standard comments. This macro is available under `comment, todo, fixme, /*` tag names. Yes, `<@/*>this is a comment, even if it looks weird.</@/*>` As with any macro, it can include nested macro tags with the same name, but they have to be properly opened and closed; if you want to use multiple nested comment macros, use different case (might just work, especially if the parser is strict) or their aliases to make sure that the commented-out text can contain invalid tags and whatnot.
- Multiple skins support. You can specify which skin should be used for which widget with `skin` tag attribute. If attribute is not passed, skin mapped to default key is used. Note that some attributes rely on skin usage (for example: `color` attribute, which needs a skin to find a color by name), but do not have direct access to the skin used to construct the widget, as most actors do not keep that reference. It is safe to assume that skin attribute should be used ONLY to select the initial style of the widget and other skin-using-attributes will still rely on default skin.
- `@evaluate` and `@actor` macros got more powerful. Now you can pass string attributes to your methods - text between evaluation macro tags will be passed as method arguments. See `EvaluateLmlMacroTag` and `ActorLmlMacroTag` docs for examples and more info.
- Proper condition parsing. Now `@if` macro can look like this: `<@if ($someAction == 13) || ({someString} < 7) || ((--{loop:index}) == (++{for:index}))>`. Since calculations are so powerful right now, `@calculate` macro was added: it works pretty much like an `@assign` macro, except it expects an equation instead of literal value. Result of the equation will be assigned to a parser `{attribute}`. See `ConditionalLmlMacroTag` and `CalculationLmlMacroTag` for more info.
- `@replaceArguments` macro. This allows to locally override LML `{arguments}`, by assigning new values with its attributes. Passing no attributes to the macro is a safe no-op. See `ArgumentReplacementLmlMacroTag` docs. 
- `@exception` macro. Ever wanted to throw an exception with a meaningful message from within LML template without dirty hacks like `</iShouldNeverBeThere/>`? This macro does exactly that. It expects one (optional) boolean attribute: if it is true, exception is always thrown; if false, exception is thrown only if parser is strict. Content between macro tags is appended as exception reason message.
- `@newTag` macro. Now you can create new `<actorTags>` from within LML templates, not only `<@macros>`. First argument is tag names array, second is a method ID that takes an instance of LmlActorBuilder (contains skin, style) and returns an actor. All attributes that apply to the actor's superclass will also apply to your custom actor. For example, if you return an instance of Table, it will be able to have children (like a regular Table) and all cell/table attributes will be honored. Before, you had to create custom tag parsers, now you just have to define one method and invoke a macro. More informations in NewTagLmlMacroTag. There is also `@newAttribute` macro, but to be honest - with the new attribute parsing syntax (`LmlAttribute` interface), there's little reason to use it, as creating attributes directly in Java requires almost as much setup (which is "almost none"). Still, check NewAttributeLmlMacroTag.
- Exceptions now contain A LOT more data about the errors and add currently parsed template part to the message. In LML 0, if an exception was thrown during macro parsing, you were pretty much f&%$#d. Now, the exception will contain evaluated macro content with a separate line counter, allowing you to determine which macro's line was problematic.
- Support for previously unavailable widgets: Container, Touchpad, SelectBox, ImageButton, ImageTextButton. This basically covers every notable non-meant-to-be-semi-abstract widget in Scene2D.
- `onClose` tag attribute. As opposed to `onCreate` (which was available before), `onClose` actions are invoked after widget's tag is closed and all its children are appended, rather than invoking the action right after the widget instance is created. This is very convenient for complex tags with multiple children, like trees and tables. There are probably a few things missing, overlooked in the huge Scene2D, but remember that using  these two attributes you can always set pretty much anything that Scene2D allows to change - create a method that consumes an instance of the actor type you want to modify, do your stuff in Java and reference the method in the LML template.
- Updated example project: now it has all tags and macros explained. Make sure to check it out!

Quick LML 0-to-1 template conversion guide:

- Replace `${` with `{` (`$` marker removed from arguments).
- Replace `&` with `$` (action marker changed to `$`).
- Replace `:` with `=` in loops (assignment markers unified).
- Replace `-` with `,` in ranges (range separator changed to easily support negative ranges).
- Replace `<row>` tags (and its old aliases) with `<@row>` macro.

Unless you've used some weird hacks or unusual conditions in `if` macro (which now is a lot more powerful and doesn't support all of its old hacky operators), your old templates should work after these changes.

0.8 -> 0.9.1.7.0-SNAPSHOT:

Use snapshot for the "new" features if you really want to stick with LML 0. The last "stable" LML 0 version is 0.8.1.7.0.

- `@actor` macro. Looks for an action with the passed ID, invokes it (expecting an Actor) and adds the returned Actor to the current parent tag. For example, if you use `<@actor myMethodName />` tag inside `<table>...</table>` tags, your actor returned by `myMethodName` action will be added to the table. Useful if you want to create an actor from scratch without adding a new actor tag parser.
- Array-parsing macros (*forEach*, *nested*, etc.) now support methods that return object arrays. Before you had to manually convert arrays to iterables to properly handle method invocations; now simple object arrays can be returned and each of its elements will be safely converted to string. Primitive arrays are not supported (yet?).
- Now conditional macros (like *if*, *notNull*) evaluate to "false" if they receive no arguments, rather than throwing an exception. This might be especially useful when using nullable LML arguments (for example, `${someEmptyStringArgument}` would cause to print part after `<@if:else/>` when passed to *if* macro: `<@if ${someEmptyStringArgument}>`).
- Removed custom tooltips (which were kind of cool with their moving along with the mouse and jumping when near an edge, but still - now there's "native" support). Since LibGDX tooltips are not actors, `tooltipId` attribute is no longer supported. Old tooltip tags still work: any actor can have a TextTooltip attached with a customized style by using `tooltip` (content) and `tooltipStyle` (name of TextTooltipStyle in skin) attributes; if `tooltip` is an action (`&proceededWithThat`) and the action returns an Actor, `Tooltip<Actor>` will be created instead. As a separate tag, `<tooltip>...</tooltip>` still works like a table - internally, `Tooltip<Table>` will be created and all new tags between the tooltip entities will be added to the table; all table attributes are supported, so you can specify `background`, `oneColumn`, `tablePad`, etc. Tooltip tag can have two additional attributes: `always` and `instant`, which trigger its setter methods (as you might expect).