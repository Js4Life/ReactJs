@ECHO OFF
DEL .\tdb\*.*  /F /S /Q
CLS
IF "%~1"=="" (
  GOTO DEFAULT
) ELSE (
  GOTO PROVIDED
)

:PROVIDED
SET ONTOLOGY_LOC=%1
GOTO START

:DEFAULT
SET ONTOLOGY_LOC=c:/one/sandbox/parabole/parabole-semantics-builder/src/main/resources/ontology/finance
GOTO START

:START

@ECHO OFF
DEL .\tdb\*.*  /F /S /Q
CLS
IF "%~1"=="" (
  GOTO DEFAULT
) ELSE (
  GOTO PROVIDED
)

:PROVIDED
SET ONTOLOGY_LOC=%1
GOTO START

:DEFAULT
SET ONTOLOGY_LOC=c:/sandbox/parabole/parabole-semantics-builder/src/main/resources/ontology/finance
GOTO START

:START

CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/Parabole-Model.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/Policy_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/CriticalDataElements.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/meeting.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/a_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/ccar_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/mpbroot_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/regulation.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/DSL_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/liqdata.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/common.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/currency.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/Bank.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/depositor.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/entity.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/location.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/naics.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/obligor.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/product-segment.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/cecl_structure.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/cecl_2016.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/pnc_cecl.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/fasb_concepts.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/basel_glossary.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/basel_vanilla.rdf
ECHO =====================================================
 