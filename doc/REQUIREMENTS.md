# Vizsgaremek

## Vízió

A cél, hogy valósítsátok meg az egyéni ötleteteket!

## Feladat nagysága

* Legalább két tábla, 1-n kapcsolatban (lehet n-n is)
* Legalább két SQL migráció
* Legalább két entitás
* Legalább egy service
* Legalább egy controller
* Minden HTTP metódusra legalább egy végpont (`GET`, `POST`, `PUT`, `DELETE`)

## Nem-funkcionális követelmények

Klasszikus háromrétegű alkalmazás, MariaDB adatbázissal,
Java Spring backenddel, REST webszolgáltatásokkal.

Követelmények tételesen:

* Hozz létre egy `README.md` fájlt, ami tartalmaz egy rövid leírást a projektről
* SQL adatbázis kezelő réteg megvalósítása Spring Data JPA-val (`Repository`)
* Flyway/Liquibase - a scriptek a funkciókkal együtt készüljenek, szóval ahogy bekerül az entitás, úgy kerüljön be egy
  plusz script is, ami a táblát létrehozza
* Üzleti logika réteg megvalósítása `@Service` osztályokkal
* Integrációs tesztek megléte (elég WebClient tesztek), legalább 80%-os tesztlefedettség
* Controller réteg megvalósítása, RESTful API implementálására. Az API végpontoknak a `/api` címen kell elérhetőeknek lenniük.
* Hibakezelés, validáció
* Swagger felület
* HTTP fájl a teszteléshez
* Dockerfile
* Új privát repository-ba kell dolgozni az organization-ön belül, melynek címe `java-sv3-adv-project-VezetéknévKeresztnév`
* Commitolni legalább entitásonként, és hozzá tartozó REST végpontonként


## Vizsgaremek témaötletek

Ezek csak ötletek, ezen kívül is bármi szabadon választható. Ebben az esetben egyeztess valamelyik mentorral!

1. oltópont, 
2. webshop, 
3. idősek otthona (gyógyszerek, ételek, időpontok), 
4. vállalatirányítási rendszer (számlák, rendelések), 
5. munkaidő-nyilvántartó rendszer, 
6. munkaerő-nyilvántartó rendszer, 
7. kutatások nyilvántartása, 
8. flottakezelő rendszer, 
9. autókereskedés, 
10. filmes adatbázis, 
11. állatkereskedés, 
12. menhelyi nyilvántartó, 
13. eszközleltár, 
14. biztosítási rendszer (szerződések, káresemény, biztosítási összeg, biztosítás fajtája), 
15. foglalási rendszer, 
16. utazási iroda, 
17. építőipari projekt rendszere, 
18. könyvtári nyilvántartó, 
19. egyéni költségeket nyilvántartó rendszer, 
20. valutaváltó, 
21. vonatjegy applikáció, 
22. ételrendelő applikáció, 
23. szolgáltatásrendelő (fodrászat, időpontkezelés, szolgáltatások), 
24. mesterember app (kit, mikor lehet hívni),
25. tanulónyilvántartó rendszer (NEPTUN),
26. kedvezményes kuponokat gyűjtő app (milyen termék, milyen kedvezmény),
27. ticketing (hibabejelentő rendszer),
28. online kurzusok (e-learning menedzsment rendszer, LMS),
29. telefonflotta nyilvántartása,
30. elektromos művek ügyfélnyilvántartója,
31. nyelvtanár-nyilvántartó,
32. ételkiszállítás/ételfutár app (Teletál, Netpincér),
33. konferencia-nyilvántartó,
34. receptgyűjtemény.
