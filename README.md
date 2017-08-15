# PLSQLSourceExtractor
## A tool to extract oracle pl/sql code

Now it extracts packages, procs, funcs and types

To start extracion:
  1. Set up connection url in DEV_CONN.xml (user in url must have permissions to select from dba_source )
  1. Fill space separatated object list(or list of masks in SQL like style) in Objs1.xml 
        1. First part in object element is schema owner than dot and object mask e.g. **OWNER.PACKAGE%**
  1. Fill out folder for this object group
  1. Run java -jar PLSQLSrcExt.jar DEV_CONN.xml Objs1.xml
