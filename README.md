[![Maven Central](https://img.shields.io/maven-central/v/com.github.xmlet/xsdParser.svg)](https://search.maven.org/#artifactdetails%7Ccom.github.xmlet%7CxsdParser%7C1.0.6%7Cjar)
[![Build](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdParser&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser)
[![Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdParser&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=com.github.xmlet%3AxsdParser)
[![Vulnerabilities](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdParser&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser)
[![Bugs](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdParser&metric=bugs)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser)

# XsdParser


<div style="text-align: right"> 
    XsdParser is a library that parses a XML Definition file (.xsd) into a list of java objects. Each different tag has a corresponding java object
     and its attributes are represented as fields in java. All these objects derive from the same abstract element, XsdAbstractElement.
    All java representations of the xsd elements follow the schema definition for xsd elements.  
    For example, the xsd:annotation tag only allows xsd:appinfo and xsd:documentation as children nodes, and also can have an attribute named id, therefore
    XsdParser has the following class (simplified for example purposes):  
</div>  

<br />  

```java
public class XsdAnnotation extends XsdIdentifierElements {

    //The id field is inherited from XsdIdentifierElements.
    private List<XsdAppInfo> appInfoList = new ArrayList<>();
    private List<XsdDocumentation> documentations = new ArrayList<>();
}
```

The set of rules followed by this library can be consulted in the following URL:

http://www.datypic.com/sc/xsd/s-xmlschema.xsd.html

## Installation

First, in order to include it to your Maven project, simply add this dependency:

``` xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>xsdParser</artifactId>
    <version>1.0.6</version>
</dependency>
```

## Usage example

Here follows a simple example:

``` java
public class ParserApp {
    public static void main(String [] args) {
        String filePath = "Your file path here.";
        XsdParser parserInstance = new XsdParser();

        Stream<XsdElement> elementsStream = parserInstance.parse(filePath);
    }
}
```
<div style="text-align: right"> 
After parsing the file like shown above it's possible to start to navigate in the resulting parsed elements. In the image below it is 
 presented the class diagram that could be useful before trying to start navigating in the result. There are multiple abstract classes
  that allow to implement shared features and reduce duplicated code. 
</div>

<br />

![image](https://raw.githubusercontent.com/xmlet/XsdParser/master/src/main/java/org/xmlet/xsdparser/xsdelements/xsdelements.png) 

### Navigation

<div style="text-align: right"> 
A simple example of a xsd file is presented with the correspondent code that shows how to navigate in the element list produced by the
parse function.
</div>

<br />

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

The result could be consulted in the following way:

``` java
public static void main(String [] args) {
    (...)
    
    List<XsdElement> elements = elements.collect(Collectors.toList());
            
    XsdElement htmlElement = elements.get(0);

    XsdComplexType htmlComplexType = htmlElement.getXsdComplexType();
    List<XsdAttribute> htmlAttributeList = htmlComplexType.getXsdAttributes().collect(Collectors.toList());

    XsdAttribute manifestAttribute = htmlAttributeList.get(0);

    XsdChoice choiceElement = (XsdChoice) htmlComplexType.getXsdChildElement();

    List<XsdAbstractElement> choiceChildren = choiceElement.getXsdElements().collect(Collectors.toList());

    XsdGroup flowContentGroup = (XsdGroup) choiceChildren.get(0);

    XsdAll flowContentAll = (XsdAll) flowContentGroup.getChildElement();

    List<XsdAbstractElement> groupMembers = flowContentAll.getXsdElements().collect(Collectors.toList());

    XsdElement elem1 = (XsdElement) groupMembers.get(0);
}
```

### Reference solving

<div style="text-align: right"> 
This is a big feature of this library. In XSD files the usage os the ref attribute is frequent, in order to avoid repetition of xml code. 
This generates two problems when handling the parsing which are detailed below.
</div>

#### Missing ref elements

<div style="text-align: right"> 
The referenced element does not exist. Even though it should not happen, it might. In order to deal with this problem there were created wrappers to all elements.
</div>

<br />

<div style="text-align: right">
UnsolvedElement - A wrapper to all elements that were not found in the file.
</div>

<div style="text-align: right">  
ConcreteElement - A wrapper to all elements that are present in the document.  
</div>

<div style="text-align: right">
NamedConcreteElement - A wrapper to all elements that are present in the document and have a name attribute present.  
</div>

<div style="text-align: right">
ReferenceBase - A shared interface from which UnsolvedReference and ConcreteElement derive.  
</div>

<br />

<div style="text-align: right">
Any remaining UnsolvedReferences can be consulted after the file parsing using the method <i>getUnsolvedReferencesForFile</i> passing the full path of the parsed file. Those 
are the references that were not in the file and the user of the XsdParser library should resolve it by either adding the missing elements to the file or 
just acknowledging that there are elements missing.
</div>

#### Parsing Strategy

<div style="text-align: right"> 
In order to minimize the number of passages in the file, which take more time to perform, this library chose to parse all the elements and then resolve the references present. 
This means that after parsing all the elements from the file, those same elements are filtered and obtained all the NamedConcreteElements. Those are the elements that may be 
referenced by UnsolvedReferences. This way, we can compare the name present in the NamedConcreteElement and the ref in the UnsolvedElement. In the case that a match is present a 
deep copy of the element wrapped in the NamedConcreteElement is made and accessing the parent of the element wrapped in UnsolvedElement the change can be made. 
</div>

<br />

Short Example:

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
When replacing the UnsolvedReferences the XsdGroup with the ref attribute is going to be replaced by a deep copy of the already parsed
XsdGroup with the name attribute. This is achieved by accessing the parent of the element to be replaced and removing the element to be replaced
and adding the replacement.
</div>

### Hierarchy support

<div style="text-align: right"> 
The parser supports xsd:base tags, which allow the use of hierarchies in the xsd files. 
The extended xsd:element is referenced in the element containing the xsd:base tag so in order to obtain all the attributes/elements that element has the user  
has to iterate on that base field.
</div>

## Code Quality

<div style="text-align: right"> 
There are some tests available using the HTML5 schema and the Android layouts schema, you can give a look at that examples and tweak them in order to gain a better understanding of how the parsing works.
The tests also cover most of the code, if you are interested in verifying the code quality/vulnerabilities/etc the various metrics are available on:
</div>

<br />

https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdParser  
  
## Important remarks

<div style="text-align: right"> 
<b>xsd:import</b> - XsdParser does not support xsd:import tags, meaning that if there are import tags the content of the imported files should be manually added to the main file. Support for xsd:import tags will be added, when my schedule clears.
</div>     

<div style="text-align: right">
<b>name attribute</b> - XsdParser uses the name attribute as a tool in order to resolve references, therefore it should be unique in the file. Using multiple times the same name will generate unexpected behaviour.  
</div>