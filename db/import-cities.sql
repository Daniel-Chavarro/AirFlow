LOAD DATA INFILE '/var/lib/mysql-files/worldcities.csv'
    INTO TABLE cities
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 ROWS
    (name, country, code, lat, lng);
