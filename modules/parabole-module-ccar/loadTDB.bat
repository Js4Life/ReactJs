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
SET ONTOLOGY_LOC=c:/one/sandbox/parabole/parabole-semantics-builder/src/main/resources/ontology/finance
GOTO START

:START

CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/Parabole-Model.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/Parabole-Model-Finance.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/Policy_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/ObligorFin.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/ObligorInf.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/PPNRProject.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/PPNRLineItemID.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/ProductSegment.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/CriticalDataElements.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/meeting.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/a1.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/a_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/assetclassification.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/assets.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/assets_liability.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/bank.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/capitalmanagement_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/ccar_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/creditriskmanagement_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/fed_assetclassification.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/fry-14a_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/fry-14q_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/fry-9c_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/hqla_data.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/hqla_main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/hqla_securities.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/lcr.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/mpbroot_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/portfoliomanagement_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/product.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/referencedata.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/regulation.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/Y14DB_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/OneData_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/ACBS_CUSTOMER_MMDDYYYY.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/ACBS_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/DSL_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/Bank_Main.rdf
ECHO =====================================================
CALL tdbloader --loc=tdb   %ONTOLOGY_LOC%/PPNRProject.rdf
ECHO =====================================================