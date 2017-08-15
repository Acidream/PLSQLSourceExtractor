# PL/SQL Sources Extractor
## A tool to extract oracle pl/sql code from database

Now it extracts packages, procedures, functions and types

To start extraction:
  1. Set up connection url in DEV_CONN.xml (user in url must have permissions to select from dba_source )
  1. Fill space separated object list(or list of masks in SQL like style) in Objs1.xml 
        1. First part in object element is schema owner than dot and object mask e.g. **OWNER.PACKAGE%**
  1. Fill out folder for this object group
  1. Run java -jar PLSQLSrcExt.jar DEV_CONN.xml Objs1.xml
