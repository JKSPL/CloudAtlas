Jak odpalić:

Dodajemy target/classes do CLASSPATH, żeby RMI widziało klasy
Odpalamy registry.sh
Dalej odpalamy Fetchera, QuerySignera i Agenta w dowolnej kolejności.
Przy odpaleniu muszą mieć swoje pliki konfiguracyjne.

Wszystkim programom co udostępniają coś przez RMI musimy podlinkować im jakieś policy.

Fetcher wystawia swoje dane przez RMI
Żeby fetcher był widziany przez inne komputery trzeba dodać Djava.rmi.server.hostname=<ip>
Możemy mu zmienić updateinterval w config.properties

QuerySigner wystawia swoje restowe API przez port wyspecyfikowany kluczem apiport w config.properties - tworzy dwa klucze:
    config.privatekey
    config.publickey
Używam tego samego podpisu do instalacji/dezinstalacji query. 
QuerySigner sprawdza czy jest query syntaktycznie poprawne i czy query o takiej nazwie już nie istnieje.
Możliwe API to tylko /sign/, czyta dwa pola POST name i query, jak jest OK, to zwróci podpis w base64 wpp pusty string.
Przez RMI też wystawia API, ale nikt tego nie używa.
 

config.publickey kopiujemy tam gdzie jest config.properties Agenta, żeby Agent wiedział czyje podpisy przyjmować.


Agentowi można dać inne config.properties niż domyślne przez: -c config1.properties
można też dać jakąś liczbę executorów inną niż 2 np.: -e 5
Agent ma swoje RMI tak jak gdzieś tam w poleceniu, ale tego w ogóle nie używam.
Agent wystawia swoje restowe API analogicznie jak QuerySigner na porcie z config.properties:
    /install/ - instaluj query
    /uninstall/ - deinstaluj query
    /zmis/ - jakie mam zmi
    /getzmidata/ - wypisuje dane danego zmi
    /getqueries/ - wypisuje query jakie mam
    /addcontact/ - dodaje fallback
    
Agentowi można dawać fallback contacty przez stdin np. w postaci:
/army1/division1/regiment2 37.8.226.112 8128
Nie sanityzuję stdin więc trzeba uważać.

Agent nasłuchuje na porcie server_port z configa. Trzeba mu też jego IP podać,
żeby wiedział jakim nadawcą podpisać swoje pakiety.
 
Agent fragmentuje większe wiadomości na MessageBloby. Jak jeden z serii nie dojdzie to wywala wszystkie z serii 
po destroyperiod z configa.
  
Agent trzyma instalowanequery/revokedquery i stampy na nich. W ten sposób wie co odpalać, bierze nowsze info.

Wszystkie strategie plotkowania zostały zaimplementowane, wybiera się je w configu.

GTP jest zaimplementowane, doklejam do wiadomości co najwyżej 4 timestampy i na podstawie tego wiem jaki time offset
wybrać.

Agentowi podajemy adres ip fetchera w agentserver w configu.

Nie wiem jak się odpala bez IntelliJ, ale można ściągnąć IntelliJ, wgrać mój projekt i ustawić odpowiednie flagi
do RMI przy odpalaniu. Czasami bez powodu się ustawia Java 1.5 zamiast 1.8, która jest potrzebna

Klient jest napisany w C#, więc pewnie działa tylko na windowsie. Podaje mu się w configu IP agentów i IP CA 
i można sobie przeglądać wykresy. Nie wiem jak się go odpala bez Visual Studio, ale mogę np. binarki podesłać.

Fetcher działa tylko na ubuntu, ale QuerySigner i Agent już dowolnie.

Projekt używa mavena, więc to pewnie jakieś proste, jak będę miał prezentować to się przed zajęciami dowiem 
jak to odpalać w labach.

