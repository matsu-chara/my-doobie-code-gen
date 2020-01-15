# sql code gen

output sql fragment for doobie from clipboard DDL.
this is just sample code repository.


```
$ cat <<EOS | pbcopy
CREATE TABLE user(
    id int NOT NULL,  -- 従業員番号
    last_name varchar(255), -- 名
    first_name varchar(255), -- 姓
    PRIMARY KEY (no, department_no)
);
EOSCREATE TABLE user(
    id int NOT NULL,  -- 従業員番号
    last_name varchar(255), -- 名
    first_name varchar(255), -- 姓
    PRIMARY KEY (no, department_no)
);

$ sbt genSqlFromClipBoard
```
