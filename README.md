# TimeSheet - Vizsgaremek

Készült az [Újratervezés Program 3.0](https://ujratervezes.nive.hu/projekt) Junior Vállalati Java backend tanfolyamán a képesítő vizsgához. A követelmények [itt](/doc/REQUIREMENTS.md) találhatók.

## Leírás

Ez a szoftver csomag egy vállalati környezetbe szánt munkaidő nyilvántartó rendszer hátter komponense. Segítségével szervezeti egységekben dolgozók időfelhasználását lehet rögziteni, módositani, beszámolókhoz adatot szolgáltatni.

---

## Felépítés

Az applikáció entitásokból (Worker, Department, TimeEntry) és ezek interakcióiból épül fel.

![Entity relationships](/doc/entities.png)

## Department entitás

A `Department` entitás reprezentálja a vállalat egy szervezeti egységét, ahol a munkát végző személyek ugyanazon vezető alá tartoznak.

### Az entitás a következő attribútumokkal rendelkezik:

* `id` - egyedi azonosito, pl. `5`
* `department_name` - az egység neve, pl. `Finance`, nem lehet ures
* `managerId` - a szervezeti egységet vezető személy - referencia egy `Worker` entitás peldányra, lehet `null` is

A `Department` és a `Worker` entitások között egyirányú, **n:1** számosságú `managed-by` kapcsolat van, melyet a `Department.managerId` attribútum tárol.

#### példa entitás JSON formatumban:
```JSON
{
  "id": 3,
  "name": "Production",
  "managerId": 5
}
```

#### végpontok: 

| HTTP metódus | Végpont               | Leírás
| ------------ | --------------------- | ------
| POST         |`/api/departments`     | létrehoz egy uj szervezetet
| GET          |`/api/departments`     | lekérdezi az összes szervezetet
| GET          |`/api/departments/{id}`| lekérdez egy szervezetet az `id` egyedi azonositoja alapján
| PUT          |`/api/departments/{id}`| egy szervezet attribútumainak felulirasa az `id` egyedi azonositoja alapján
| PUT          |`/api/departments/{departmentId}/appoint-department-manager/{workerId}` | személy hozzarendelese szervezethez egyedi azonositok alapján

## Worker entitás

A `Worker` entitás egy olyan személyt reprezental, aki a vallalat szamara munkat vegez szerzodeses kapcsolatban.

### Az entitás a következő attribútumokkal rendelkezik:

* `id` - egyedi azonosito, pl. `2`
* `name` - a személy neve, nem lehet ures
* `dateOfBirth` - a személy szuletesi datuma, nem lehet ures, mulbeli erteke lehet csak
* `contractType` - a vallalattal fennallo szerzodeses kapcsolat tipusa. A kovetkezo ertekeket veheti fel: `FULL_TIME_EMPLOYEE`, `PART_TIME_EMPLOYEE`, `TIME_AND_MATERIAL`, `FIXED_AMOUNT`
* `departmentId` - a vallalat ezen szervezeti egységehez van hozzarendelve a személy - referencia egy `Department` entitás peldányra, lehet `null` is

A `Worker` és a `Department` entitások között egyirányú, **n:1** számosságú `assigned-to` kapcsolat van, melyet a `Worker.departmentId` attribútum tárol.

#### példa entitás JSON formatumban:
```JSON
{
  "id": 2,
  "name": "Teszt Elek",
  "dateOfBirth": "2000-01-01",
  "contractType": "FULL_TIME_EMPLOYEE",
  "departmentId": 3
}
```

#### végpontok: 

| HTTP metódus | Végpont                 | Leírás
| ------------ | ----------------------- | ------
| POST         |`/api/workers`           | létrehoz egy uj személyt
| GET          |`/api/workers`           | lekérdezi az összes személyt
| GET          |`/api/workers/{id}`      | lekérdez egy személyt az `id` egyedi azonositoja alapján
| PUT          |`/api/workers/{id}`      | egy személy attribútumainak felulirasa az `id` egyedi azonositoja alapján
| PUT          |`/api/workers/{workerId}/assign-to-department/{departmentId}` | szervezet hozzarendelese személyhez egyedi azonositok alapján
| DELETE       |`/api/workers/{id}`      | egy személy eltávolitása az `id` egyedi azonositoja alapján

---

## TimeEntry entitás

A `TimeEntry` entitás egy munka naplo bejegyzest reprezental, tartalmazza, hogy ki vegezte a munkat, milyen szervezeti egységben, mikor kezdte és mennyi ideig tartott.

### Az entitás a következő attribútumokkal rendelkezik:

* `id` - egyedi azonosito, pl. `452`
* `departmentId` - a vallalat ezen szervezeti egységehez van hozzarendelve a személy - referencia egy `Department` entitás peldányra, nem lehet `null`
* `workerId` - a munkat elvegzo személy - referencia egy `Worker` entitás peldányra, nem lehet `null`
* `startDateTime` - a munka kezdetenek idopontja, pl. `2023-04-19T09:00`
* `durationInMinutes` - a munka hossza, pl. 8 oras munkaidore: `480`
* `description` - a munka leirasa, pl. `writing project documentation`

A `TimeEntry` és a `Worker` entitások között egyirányú, **n:1** számosságú `worked-by` kapcsolat van, melyet a `TimeEntry.workerId` attribútum tárol.

A `TimeEntry` és a `Department` entitások között egyirányú, **n:1** számosságú `worked-at` kapcsolat van, melyet a `TimeEntry.departmentId` attribútum tárol.

#### példa entitás JSON formatumban:
```JSON
{
  "id": 452,
  "departmentId": 3,
  "workerId": 2,
  "startDateTime": "2023-04-19T09:00",
  "durationInMinutes": 480,
  "description": "machine maintenance"
}
```
#### végpontok: 

| HTTP metódus | Végpont               | Leírás
| ------------ | --------------------- | ------ |
| POST         |`/api/timesheetentries`     | létrehoz egy uj bejegyzést
| GET          |`/api/timesheetentries`     | lekérdezi az összes bejegyzést, lehetseges paramétereket lasd alabb
| GET          |`/api/timesheetentries/{id}`| lekérdez egy bejegyzést az `id` egyedi azonositoja alapján
| PUT          |`/api/timesheetentries/{id}`| egy bejegyzés attribútumainak felulirasa az `id` egyedi azonositoja alapján
| DELETE       |`/api/timesheetentries/{id}`| egy bejegyzés eltávolitása az `id` egyedi azonositoja alapján

A `GET /api/timesheetentries` végpont bejegyzések lekérdezéséhez használhato paraméterei:

| Paraméter         | Leírás |
| ----------------- | ------ |
| `departmentId`    | a `departmentId` attributum ezt az erteket tartalmazza
| `workerId`        | a `workerId` attributum ezt az erteket tartalmazza
| `dateFrom`        | a `startDateTime` attributum legalabb ekkor kezdodott
| `dateTo`          | a `startDateTime` attributum legfeljebb ekkor kezdodott
| `descriptionPart` | a `description` attributum tartalmazza ezt a karakter sorozatot

példaul:
```C
/api/timesheetentries?departmentId=2&workerId=4&dateFrom=2023-04-17&dateTo=2023-04-23&descriptionPart=maintenance
```
---

## Technológia

Az applikáció egy [Java](https://en.wikipedia.org/wiki/OpenJDK) nyelven irt [back-end API](https://en.wikipedia.org/wiki/Frontend_and_backend#API) program. Az adatok tárolása egy [Docker](https://en.wikipedia.org/wiki/Docker_(software)) kontenerben futo [MariaDB](https://en.wikipedia.org/wiki/MariaDB) adatbázis szerverre van bizva.

A program három retegbol áll:
- also tárolo reteg (Repository) a hattertarral valo kommunikacioert felel.
- a kozepso szerviz reteg (Service) kerest kap a kontrollertol, kerest kuld a tárolo retegnek, feldolgozza a kapott valaszt és valaszt kuld a kontrollernek,
- a felso kontroller reteg (Controller) [HTTP](https://en.wikipedia.org/wiki/HTTP) kerest fogad a webszervertol, a lekerdezi a szervizt, feldolgozza a valaszat és HTTP valasz uzenetet kuld a webszervernek
A retegek kozotti adat mozgatast, az adatbázis specifikus kommunikaciot a Spring keretrendszer biztositja. Az ugyfel programtol erkezo HTTP keresek kiszolgalasat az applikációba becsomagolt [Tomcat](https://tomcat.apache.org/) szerver latja el.

Az egesz programot egy (második) Docker kontenerbe lehet becsomagolni, amely kommunikal az elso kontenerben levo adatbázissal.

Az integracios tesztekhez egy memoriaban futo [H2](https://www.h2database.com/) adatbázis indul el.

Az applikáció rendelkezik egy [Swagger UI](https://swagger.io/tools/swagger-ui/) API dokumentacios interfesszel, amellyel egy generalt weboldalon keresztul interakcioba lephetunk az alkalmazassal.

Az ugyfel programtol erkezo uzenetek torzseben [JSON](https://www.json.org/json-en.html) formatumu adatokat var és JSON formatumban valaszol, legyen az adat vagy hibauzenet.

## Futtatás lokálisan
- installáld a lokális operácios rendszernek megfelelo [Java JDK](https://jdk.java.net/) programot
- installáld a lokális operácios rendszernek megfelelo [Maven](https://maven.apache.org/download.cgi) programot
- installáld a lokális operácios rendszernek megfelelo [Docker](https://docs.docker.com/get-docker/) programot
- toltsd le a forraskod csomagot és masold be egy helyi konyvtarba
- a projekt konyvtarban parancssorbol add ki a kovetkezo parancsokat:
  - `mvn clean package`
  - `docker-compose up`
- bongeszobol inditsd a kovetkezo cimet: [http://localhost:8080/](http://localhost:8080/). Itt egy udvozlo oldalra erkezel, ahol az adatbázist kiuritheted, lekerheted az entitások eltárolt egyedeinek listait valamit atlephetsz a Swagger UI feluletre, ahol kiprobalhatod az API végpontok mukodeset
- az API a `http://localhost:8080/api/...` cimen varja az ugyfelprogramtol erkezo kereseket

## Disclaimer
Az `/api/clear-repository` és `/api/add-demo-data` végpontok csak a program demonstracios jellege miatt kerult lefejlesztesre. Eles használatu programba nem kerulhet bele demo adat vagy onpusztito funkcionalitas!