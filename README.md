# Custom Hazelcast Maploader 
[Issue #16700](https://github.com/hazelcast/hazelcast/issues/16700)

The goal of this project is to allow easy configuration of a map store without the need of developing a custom implemetation. This is my proposal for this task. I use mysql as the database.

### Sample XML Configuration
We create a simple Map Configuration that looks like the one below; taking the advantage of the properties section, we define the Database Connectivity details and the table name with its column details.

```sh
<map name="country_codes">
        <in-memory-format>BINARY</in-memory-format>
        <backup-count>1</backup-count>
        <async-backup-count>0</async-backup-count>
        <time-to-live-seconds>2592000</time-to-live-seconds>
        <max-idle-seconds>2592000</max-idle-seconds>
        <eviction-policy>LFU</eviction-policy>
        <max-size policy="PER_NODE">150000</max-size>
        <eviction-percentage>25</eviction-percentage>
        <min-eviction-check-millis>100</min-eviction-check-millis>
        <merge-policy>com.hazelcast.map.merge.PutIfAbsentMapMergePolicy</merge-policy>
        <cache-deserialized-values>INDEX-ONLY</cache-deserialized-values>
        <map-store enabled="true" initial-mode="LAZY">
            <class-name>com.hazelcast.custom.SQLBasedMapStore</class-name>
            <write-delay-seconds>10</write-delay-seconds>
            <write-batch-size>10</write-batch-size>
            <write-coalescing>false</write-coalescing>
            <properties>
                <property name="key">country</property>
                <property name="value">country_code</property>
                <property name="dbdriver">com.mysql.jdbc.Driver</property>
                <property name="dburl">jdbc:mysql://localhost:3306/mydb</property>
                <property name="dbschema">mydb</property>
                <property name="dbuser">root</property>
                <property name="dbpass">pass123</property>
            </properties>
        </map-store>
    </map>
```
from this configuration:

- the database name is: mydb
- user: root
- password: pass123
- table name: country_codes
- key: country
- value: country_code