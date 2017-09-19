# PL/SQL Sources Extractor
## A tool to extract oracle pl/sql code from database

Now it extracts packages, procedures, functions, tables, views in newMethod mode(default)
 and packages, procedures, functions in -USEOLDMETHOD mode

**To start extraction:**
  1. Set up connection url in DEV_CONN.xml (user in url must have permissions to select from dba_source )
  1. Fill space separated object list(or list of masks in SQL like style) in Objs1.xml 
        1. First part in object element is schema owner than dot and object mask e.g. **OWNER.PACKAGE%**
  1. Fill out folder for this object group
  1. Run java -jar PLSQLSrcExt.jar DEV_CONN.xml Objs1.xml


**Options**

 -GENEXAMPLES - generates example configuraion files and exits 

 -NOCONF - uses comma separated list of db objects instead of conf files. All files will be saved in /NoConf folder.
            
            e.g. java -jar PLSQLSrcExt.jar -NOCONF DEV_CONN.xml ABS.EMP,ABS.FUNCTION1
                        (no spaces allowed between db objects just commas)
            
 -ADDTYPEDIR - adds folder corresponding to type of object to the end of path
 

 -USEOLDMETHOD - switches to old extraction method through dba_sources view and object list from dba_objects. By default tool uses DBMS_METADATA. 
 
 -USEONLYDBASOURCE - switches to old extraction method through dba_sources view. Object list acquired through dba_objects too.  
