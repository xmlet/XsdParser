[![Maven Central](https://img.shields.io/maven-central/v/com.github.xmlet/xsdParser.svg)](https://search.maven.org/#artifactdetails%7Ccom.github.xmlet%7CxsdParser%7C1.0.7%7Cjar)
[![Build](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdParser&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdParser&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=com.github.xmlet%3AxsdParser)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdParser&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdParser&metric=bugs)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser)

# XsdParser


<div align="justify"> 
    XsdParser is a library that parses a XML Definition file (.xsd) into a list of java objects. Each different XSD tag has 
    a corresponding Java class and the attributes of a given XSD type are represented as fields of that class. All these classes derive from the 
    same abstract class, <i>XsdAbstractElement</i>. All Java representations of the XSD elements follow the schema definition 
    for XSD. For example, the <i>xsd:annotation</i> tag only allows <i>xsd:appinfo</i> and <i>xsd:documentation</i> as children nodes, 
    and also can have an attribute named <i>id</i>, therefore XsdParser has the following class (simplified for example purposes):
    <br /> 
    <br />    
</div>  

```java
public class XsdAnnotation extends XsdAbstractElement {

    private String id;
    private List<XsdAppInfo> appInfoList = new ArrayList<>();
    private List<XsdDocumentation> documentations = new ArrayList<>();
    
    // (...)
}
```

<div align="justify"> 
    The set of rules followed by this library can be consulted in the following URL: <a href="http://www.datypic.com/sc/xsd/s-xmlschema.xsd.html">XSD Schema</a>
</div>

## Installation

<div align="justify"> 
    First, in order to include it to your Maven project, simply add this dependency:
    <br />
    <br />
</div>

```xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>xsdParser</artifactId>
    <version>1.0.14</version>
</dependency>
```

## Usage example

<div align="justify"> 
    A simple example:
    <br />
    <br />
</div>

```java
public class ParserApp {
    public static void main(String [] args) {
        String filePath = "Your file path here.";
        XsdParser parserInstance1 = new XsdParser(filePath);
        
        //or
        
        String jarPath = "Your jar path here.";
        String jarXsdPath = "XSD file path, relative to the jar root.";
        XsdParserJar parserInstance2 = new XsdParserJar(jarPath, jarXsdPath);

        Stream<XsdElement> elementsStream = parserInstance1.getResultXsdElements(filePath);
        Stream<XsdSchema> schemasStream = parserInstance1.getResultXsdSchemas(filePath);
    }
}
```
<div align="justify"> 
    After parsing the file like shown above it's possible to start to navigate in the resulting parsed elements. In the 
    image below it is presented the class diagram that could be useful before trying to start navigating in the result. 
    There are multiple abstract classes that allow to implement shared features and reduce duplicated code.
    <br />
    <br />
    <img src="https://raw.githubusercontent.com/xmlet/XsdParser/master/src/main/java/org/xmlet/xsdparser/xsdelements/xsdelements.png"/>
</div>

### Navigation

<div align="justify"> 
    Below a simple example is presented. After parsing the XSD snippet the parsed elements can be accessed with the respective 
    java code.
    <br />
    <br />
</div>

```xml
<?xml version='1.0' encoding='utf-8' ?>
<xsd:schema xmlns='http://schemas.microsoft.com/intellisense/html-5' xmlns:xsd='http://www.w3.org/2001/XMLSchema'>
	
  <xsd:group name="flowContent">
    <xsd:all>
      <xsd:element name="elem1"/>
    </xsd:all>
  </xsd:group>
	
  <xs:element name="html">
    <xs:complexType>
      <xsd:choice>
        <xsd:group ref="flowContent"/>
      </xsd:choice>
      <xs:attribute name="manifest" type="xsd:anyURI" />
    </xs:complexType>
  </xs:element>
</xsd:schema>
```

<div align="justify"> 
    The result could be consulted in the following way:
    <br />
    <br />
</div>

```java
public class ParserApp {
    public static void main(String [] args) {
        //(...)
        
        XsdElement htmlElement = elementsStream.findFirst().get();
        
        XsdComplexType htmlComplexType = htmlElement.getXsdComplexType();
        XsdAttribute manifestAttribute = htmlComplexType.getXsdAttributes().findFirst().get();
        
        XsdChoice choiceElement = htmlComplexType.getChildAsChoice();
        
        XsdGroup flowContentGroup = choiceElement.getChildrenGroups().findFirst().get();
        
        XsdAll flowContentAll = flowContentGroup.getChildAsAll();
        
        XsdElement elem1 = flowContentAll.getChildrenElements().findFirst().get();
    }
}
```

## Parsing Strategy

<div align="justify"> 
    In order to minimize the number of passages in the file, which take more time to perform, this library chose to parse 
    all the elements and then resolve the references present. To parse the XSD file we use the DOM library, which converts
    all the XSD elements into <i>Node</i> objects, from where we extract all the XSD information into our XSD respective classes.  
    <br />
    <br />
    Our parse process is also based on a tree approach, which means that when we invoke the <i>XsdSchema parse</i> function 
    the whole document will be parsed, because each <i>XsdAbstractElement</i> class extracts its respective information, i.e. a 
    <i>XsdSchema</i> instance extracts information from the received xsd:schema <i>Node</i> object, and also invokes the 
    respective parse function for each children elements present in its current <i>Node</i> object.
</div>

### Type Validations

<div align="justify"> 
    This library was born with an objective in mind, it should strictly follow the XSD language rules. To guarantee that 
    we used the Visitor pattern. We used this pattern to add a layer of control regarding different XSD types interactions.
    In the presented code snippet we can observe how this works:
    <br />
    <br />
</div>

```java
class XsdComplexContentVisitor extends XsdAnnotatedElementsVisitor {

  private final XsdComplexContent owner;
  
  @Override
  public void visit(XsdRestriction element) {
    owner.setRestriction(ReferenceBase.createFromXsd(element));
  }

  @Override
  public void visit(XsdExtension element) {
    owner.setExtension(ReferenceBase.createFromXsd(element));
  }
}
```

<div align="justify"> 
    In this example we can see that <i>XsdComplexContentVisitor</i> class only implements two methods, <i>visit(XsdRestriction element)</i>
    and <i>visit(XsdExtension element)</i>. This means that the <i>XsdComplexContentVisitor</i> type only allows these two 
    types, i.e. <i>XsdRestriction</i> and <i>XsdExtension</i>, to interact with <i>XsdComplexContent</i>, since these two
    types are the only types allowed as <i>XsdComplexContent</i> children elements. 
</div>

<div align="justify"> 
    The XSD syntax also especifies some other restrictions, namely regarding attribute possible values or types. For example
    the <i>finalDefault</i> attribute of the xsd:schema elements have their value restricted to six distinct values: 
    <br />
    <br />
    <ul>
        <li>
            DEFAULT ("")
        </li>
        <li>
            EXTENSION ("extension")
        </li>
        <li>
            RESTRICTION ("restriction")
        </li>
        <li>
            LIST("list")
        </li>
        <li>
            UNION("union")
        </li>
        <li>
            ALL ("#all")
        </li>
    </ul>
    To guarantee that this type of restrictions we use Java <i>Enum</i> classes. With this we can verify if the received 
    value is a possible value for that respective attribute.
    <br />
    There are other validations, such as veryfing if a given attribute is a positiveInteger, a nonNegativeInteger, etc. 
    If any of these validations fail an exception will be thrown with a message detailing the failed validation.
</div>

### Rules Validation

<div align="justify"> 
    Apart from the type validations the XSD syntax specifies some other rules. These rules are associated with a given XSD
    type and therefore are verified when an instance of that respective object is parsed. A simple example of such rule is the following rule: 
    <br />
    <br />
    "A xsd:element cannot have a ref attribute if its parent is a xsd:schema element."
    <br />
    <br />
    This means that after creating the <i>XsdElement</i> instance and populating its fields we invoke a method to verify 
    this rule. If the rule is violated then an exception is thrown with a message detailing the issue. 
</div>

## Reference solving

<div align="justify"> 
    This is a big feature of this library. In XSD files the usage of the ref attribute is frequent, in order to avoid 
    repetition of XML code. This generates two problems when handling the parsing which are detailed below. Either the 
    referred element is missing or the element is present and an exchange should be performed. To help in this process 
    we create a new layer with four classes:
    <br />
    <br />
    <b>UnsolvedElement</b> - Wrapper class to each element that has a <i>ref</i> attribute. <br />
    <b>ConcreteElement</b> - Wrapper class to each element that is present in the file. <br />
    <b>NamedConcreteElement</b> - Wrapper class to each element that is present in the file and has a <i>name</i> attribute present. <br />
    <b>ReferenceBase</b> - A common interface between <i>UnsolvedReference</i> and <i>ConcreteElement</i>. <br />
    <br />
    These classes simplify the reference solving process by serving as a classifier to the element that they wrap. Now we 
    will shown a short example to explain how this works:
    <br />
    <br />
</div>

```xml
<?xml version='1.0' encoding='utf-8' ?>
<xsd:schema xmlns='http://schemas.microsoft.com/intellisense/html-5' xmlns:xsd='http://www.w3.org/2001/XMLSchema'>
	
    <xsd:group id="replacement" name="flowContent">         <!-- NamedConcreteType wrapping a XsdGroup -->
        (...)
    </xsd:group>
	
    <xsd:choice>                                            <!-- ConcreteElement wrapping a XsdChoice -->
        <xsd:group id="toBeReplaced" ref="flowContent"/>    <!-- UnsolvedReference wrapping a XsdGroup -->
    </xsd:choice>
</xsd:schema>
```

<div align="justify"> 
    In this short example we have a <i>XsdChoice</i> element with an element <i>XsdGroup</i> with a reference attribute as a child. 
    In this case the <i>XsdChoice</i> will be wrapped in a <i>ConcreteElement</i> object and its children <i>XsdGroup</i> 
    will be wrapped in a <i>UnsolvedReference</i> object. When replacing the <i>UnsolvedReference</i> objects the 
    <i>XsdGroup</i> with the <i>ref</i> attribute is going to be replaced by a copy of 
    the already parsed <i>XsdGroup</i> with the name attribute, which is wrapped in a <i>NamedConcreteElement</i> object. 
    This is achieved by accessing the parent of the element, i.e. the parent of the <i>XsdGroup</i> with the <i>ref</i> attribute, 
    and replacing the <i>XsdGroup</i> with the <i>ref</i> attribute by the <i>XsdGroup</i> with the <i>name</i> attribute.
    <br />
    <br />
    Resuming the approach:
    <br />
    <ol>
        <li>
            Obtain all the <i>NamedConcreteElements</i> objects.
        </li>
        <li>
            Obtain all the <i>UnsolvedReference</i> objects. Iterate them to perform a lookup search in the previously 
            obtained <i>NamedConcreteElements</i> objects by comparing the <i>UnsolvedReference ref</i> with the 
            <i>NamedConcreteElements name</i> attributes.
        </li>
         <li>
             If a match is found, replace the <i>UnsolvedReference</i> wrapped object with the <i>NamedConcreteElements</i> wrapped object. 
         </li>
    </ol>
</div>

### Missing ref elements

<div align="justify"> 
    Any remaining <i>UnsolvedReference</i> objects can be consulted after the file is parsed by using the method 
    <i><b>getUnsolvedReferences()</b></i>. Those are the references that were  not in the file and the user of the XsdParser 
    library should resolve it by either adding the missing elements to the file or just acknowledging that there are elements missing.
</div>

### Hierarchy support

<div align="justify"> 
    This parser supports <i>xsd:base</i> tags, which allow the use of hierarchies in the XSD files. 
    The extended <i>xsd:element</i> is referenced in the element containing the <i>xsd:base</i> tag so it's possible to 
    navigate to the extended elements.
</div>

## Code Quality

<div align="justify"> 
    There are some tests available using the HTML5 schema and the Android layouts schema, you can give a look at that 
    examples and tweak them in order to gain a better understanding of how the parsing works. The tests also cover most 
    of the code, if you are interested in verifying the code quality, vulnerabilities and other various metrics, check 
    the following link:
    <br />
    <br />
    <a href="https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser">Sonarcloud Statistics</a>
</div>
  
## Important remarks

<div align="justify">
    <b>name attribute</b> - XsdParser uses the name attribute as a tool in order to resolve references, therefore it 
    should be unique in the file. Using multiple times the same name will generate unexpected behaviour.
</div>


## Changelog

### 1.0.14

<div align="justify">
    <ul>
        <li>
            Adds support for parsing XSD files inside Jar files.
        </li>
    </ul>
</div>

### 1.0.13

<div align="justify">
    <ul>
        <li>
            Project-wide documentation.
        </li>
        <li>
            Minor bug fixes.        
        </li>
    </ul>
</div>

### 1.0.12

<div align="justify">
    <ul>
        <li>
            Adds XSD complex rule validations.
        </li>
        <li>
            Adds exceptions with detailed messages, providing more information to the user.
        </li>
    </ul>
</div>

### 1.0.11

<div align="justify">
    <ul>
        <li>
            Adds attribute type validations and validations of possible values with <i>Enum</> classes.
        </li>
    </ul>
</div>