[![Maven Central](https://img.shields.io/maven-central/v/com.github.xmlet/xsdParser.svg)](https://search.maven.org/#artifactdetails%7Ccom.github.xmlet%7CxsdParser%7C1.0.7%7Cjar)
[![Build](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdParser&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser)
[![Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdParser&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=com.github.xmlet%3AxsdParser)
[![Vulnerabilities](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdParser&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser)
[![Bugs](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdParser&metric=bugs)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser)

# XsdParser


<div style="text-align: right"> 
    XsdParser is a library that parses a XML Definition file (.xsd) into a list of java objects. Each different tag has 
    a corresponding java object and its attributes are represented as fields in java. All these objects derive from the 
    same abstract element, XsdAbstractElement. All java representations of the xsd elements follow the schema definition 
    for xsd elements. For example, the xsd:annotation tag only allows xsd:appinfo and xsd:documentation as children nodes, 
    and also can have an attribute named id, therefore XsdParser has the following class (simplified for example purposes):
    <br /> 
    <br />    
</div>  

```java
public class XsdAnnotation extends XsdIdentifierElements {

    //The id field is inherited from XsdIdentifierElements.
    private List<XsdAppInfo> appInfoList = new ArrayList<>();
    private List<XsdDocumentation> documentations = new ArrayList<>();
}
```

<div style="text-align: right"> 
    The set of rules followed by this library can be consulted in the following URL:
    <br />
    <br />
    <a href="http://www.datypic.com/sc/xsd/s-xmlschema.xsd.html">XSD Schema</a>
</div>

## Installation

<div style="text-align: right"> 
    First, in order to include it to your Maven project, simply add this dependency:
    <br />
    <br />
</div>

```xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>xsdParser</artifactId>
    <version>1.0.7</version>
</dependency>
```

## Usage example

<div style="text-align: right"> 
    Here follows a simple example:
    <br />
    <br />
</div>

```java
public class ParserApp {
    public static void main(String [] args) {
        String filePath = "Your file path here.";
        XsdParser parserInstance = new XsdParser();

        Stream<XsdElement> elementsStream = parserInstance.parse(filePath);
    }
}
```
<div style="text-align: right"> 
    After parsing the file like shown above it's possible to start to navigate in the resulting parsed elements. In the 
    image below it is presented the class diagram that could be useful before trying to start navigating in the result. 
    There are multiple abstract classes that allow to implement shared features and reduce duplicated code.
    <br />
    <br />
    <img src="https://raw.githubusercontent.com/xmlet/XsdParser/master/src/main/java/org/xmlet/xsdparser/xsdelements/xsdelements.png"/>
</div>

### Navigation

<div style="text-align: right"> 
    A simple example of a xsd file is presented with the correspondent code that shows how to navigate in the element 
    list produced by the parse function.
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

<div style="text-align: right"> 
    The result could be consulted in the following way:
    <br />
    <br />
</div>

```java
public class ParserApp {
    public static void main(String [] args) {
        //(...)
        
        XsdElement htmlElement = elements.findFirst().get();
        
        XsdComplexType htmlComplexType = htmlElement.getXsdComplexType();
        XsdAttribute manifestAttribute = htmlComplexType.getXsdAttributes().findFirst().get();
    
        XsdChoice choiceElement = (XsdChoice) htmlComplexType.getXsdChildElement();
    
        XsdGroup flowContentGroup = (XsdGroup) choiceElement.getXsdElements().findFirst().get();
    
        XsdAll flowContentAll = (XsdAll) flowContentGroup.getChildElement();
    
        XsdElement elem1 = (XsdElement) flowContentAll.getXsdElements().findFirst().get();
    }
}
```

### Reference solving

<div style="text-align: right"> 
    This is a big feature of this library. In XSD files the usage os the ref attribute is frequent, in order to avoid 
    repetition of xml code. This generates two problems when handling the parsing which are detailed below.
</div>

#### Missing ref elements

<div style="text-align: right"> 
    The referenced element does not exist. Even though it should not happen, it might. In order to deal with this 
    problem there were created wrappers to each element.
    <br />
    <br />
    <b>UnsolvedElement</b> - A wrapper class to each element that was not found in the file. <br />
    <b>ConcreteElement</b> - A wrapper class to each element that is present in the document. <br />
    <b>NamedConcreteElement</b> - A wrapper class to each element that is present in the document and has a name attribute present. <br />
    <b>ReferenceBase</b> - A common interface between UnsolvedReference and ConcreteElement. <br />
    <br />    
    Any remaining UnsolvedReferences can be consulted after the file parsing using the method 
    <i>getUnsolvedReferencesForFile</i> passing the full path of the parsed file. Those are the references that were 
    not in the file and the user of the XsdParser library should resolve it by either adding the missing elements to the 
    file or just acknowledging that there are elements missing.
</div>

#### Parsing Strategy

<div style="text-align: right"> 
    In order to minimize the number of passages in the file, which take more time to perform, this library chose to parse 
    all the elements and then resolve the references present. This means that after parsing all the elements from the 
    file, those same elements are filtered and obtained all the NamedConcreteElements. Those are the elements that may be 
    referenced by UnsolvedReferences. This way, we can compare the name present in the NamedConcreteElement and the ref 
    in the UnsolvedElement. In the case that a match is present a deep copy of the element wrapped in the 
    NamedConcreteElement is made and accessing the parent of the element wrapped in UnsolvedElement the change can be made. 
    <br />
    <br />
    Short Example:
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

<div style="text-align: right"> 
    In this short example we have a XsdChoice element with an element XsdGroup with a reference attribute as a child. 
    When replacing the UnsolvedReferences the XsdGroup with the ref attribute is going to be replaced by a deep copy of 
    the already parsed XsdGroup with the name attribute. This is achieved by accessing the parent of the element to be 
    replaced and removing the element to be replaced and adding the replacement.
</div>

### Hierarchy support

<div style="text-align: right"> 
    The parser supports xsd:base tags, which allow the use of hierarchies in the xsd files. 
    The extended xsd:element is referenced in the element containing the xsd:base tag so in order to obtain all the 
    attributes/elements that element has the user has to iterate on that base field.
</div>

## Code Quality

<div style="text-align: right"> 
    There are some tests available using the HTML5 schema and the Android layouts schema, you can give a look at that 
    examples and tweak them in order to gain a better understanding of how the parsing works. The tests also cover most 
    of the code, if you are interested in verifying the code quality, vulnerabilities and other various metrics, check 
    the following link:
    <br />
    <br />
    <a href="https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser">Sonarcloud Statistics</a>
</div>
  
## Important remarks

<div style="text-align: right"> 
    <b>xsd:import</b> - XsdParser does not support xsd:import tags, meaning that if there are import tags the content of
    the imported files should be manually added to the main file. Support for xsd:import tags will be added, when my 
    schedule clears.
    <br />
    <b>name attribute</b> - XsdParser uses the name attribute as a tool in order to resolve references, therefore it 
    should be unique in the file. Using multiple times the same name will generate unexpected behaviour.
</div>