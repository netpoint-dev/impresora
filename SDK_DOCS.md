# NetPOS SDK Documentation (Consolidated)

Source: SDK20250124_V2.30/doc/API/
Extracted: 2026-05-21

---

## NFC_Defire_Ev2&Ev3_Card

demo_defireEv2&Ev3 Card
    Development manual


    Date      2021-12-08

    Version   V1.0.0
1. the command return value description
  Success: No data: Success (PS: Return value is 0)
  There is data: related command requirements data (such as: read data)
  Failure: Error: (error code)

  2. error code description
  Negative error code: (mainly PICC and PCD communication is lost, contact failure. You need to
  re-check the card)
  -2 APK layer interface Introduction parameter error
        -10 APK layer transparent error
  -1001 parameter error
  -1002 IO error
  -1003 timeout error
  -1004 Check error
  -1005 frame error
  -1006 interference error
  -1007 protocol error
  -1008 card conflict error
  -1009 send error
  16 credit error code: (card instruction operation problem)
      The format is 0x13xx, the error code is XX, "DS226031 - Product Data Sheet Mifare Desfire
  Ev2 Contactless Multi-Application IC (3.1) .pdf" Chapter VII or "Desfire EV3 User MANUAL
  3.0.pdf" Chapter 8 finds related commands , Matching this XX information

  3. the operating instructions
  1. Enter the operating interface and place the card in the identification area.
  2. Click "Open NFC", such as opening success, button "Check IC Card" can operate, continue 3.
  3. Click "Check IC Card", such as no card, the information bar displays "not retrieved to IC card"; if
  there is a card, the information bar displays the information, the command button can operate,
  you can continue to test the relevant command.
  4. The first-time EV2 / EV3 card needs to be activated to use, click the button "New Card First
  USE" to activate.
  When the application exits, the NFC is automatically turned off, and can be saheder.

  4. Precautions
      This Demo is commanded, this parameter is only example, if you need to modify, please
  refer to Manual "DS226031 - Product Data Sheet Mifare Desfire Ev2 ContactLi-Application IC
  (3.1) .pdf" or "Desfire EV3 User Manual 3.0.pdf"



                                                     1
5. Operation analysis
Note: Verify an instruction of the EV2 card requires prerequisites, such as AuthenticateEv2First,
first need to call SelectApplication, and successfully execute the AuthenticateEv2First to
complete the verification. So each command operation in DEMO is integrated, the input
parameter is 16, and below is an instructions for each operation in the test interface.


1. AuthenticateEV2First in Demo
00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
        ①                                      ②
① ：SelectApplication parameters, select application
② ：AuthenticateEv2First parameters, this parameter consists of: Key (16 bytes) + keyno (1
    byte) + lencap (1 byte), the default is 0x00
return value:
Success: Success
Failure: Error: (error code)


2. AuthenticateEV2NonFirst in Demo
00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
   ①                                      ②
① ：SelectApplication parameters, select application
② ：The parameters of Authenticateev2nonfirst, this parameter consists of: key (16 bytes) +
    Keyno (1 byte), the default is 0x00
return value:
Success: Success
Failure: Error: (error code)


3. FreeMem in Demo
return value:
Success: 3 Bytes data
Failure: Error: (error code)


4. Format in Demo
return value:
Success: Success
Failure: Error: (error code)



                                                   2
5. SetConfiguration in Demo
00 00 00 00 00
    ①       ②
①       ：SelectApplication parameters, select application
②       ：SetConfiguration parameters, this parameter consists of option (1 byte) + Data (1
    byte)
return value:
Success: Success
Failure: Error: (error code)


6. GetVersion in Demo
return value:
Success: 28 Bytes
Failure: Error: (error code)


7. GetCardUID in Demo
return value:
Success: n bytes' data (N is determined by the card, generally 7 bytes)
Failure: Error: (error code)


8. ChangeKey in Demo
00 00 00 00 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00 00 00 00 00 00 00
    ①                                    ②
①       ：SelectApplication parameters, select application
②       ：ChangeKey's parameters, composition: ISEqualgetKeyno (1 byte) + KeyNO (1 byte) +
NEWKEY (16 Bytes) + Oldkey (16 bytes)
return value:
Success: Success
Failure: Error: (error code)


9. ChangeKeyEV2 in Demo
00 00 00 00 00 12 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
    ①                                    ②
①       ：SelectApplication parameters, select application

                                                 3
②       ：ChangeKeyev2 parameters consisting: ISEqualgetKeyno (1 byte) + Keysetno (1 byte) +
KeyNO (1 byte) + NEWKEY (16 BYTES) + Oldkey (16 bytes)
return value:
Success: Success
Failure: Error: (error code)


10. InitializeKeySet in Demo
00 00 01 01 02
    ①      ②
①：SelectApplication parameters, select application
②：InitializeKeyset parameters, Composition: KeySetNo (1BYTE) + KeySetType (1byte)
return value:
Success: Success
Failure: Error: (error code)


11. FinalizeKeySet in Demo
00 00 01 01 01
    ①      ②
①：SelectApplication parameters, select application
②：FinalizeKeyset parameters, Composition: KeysetNo (1BYTE) + KeySetVersion (1byte)
return value:
Success: Success
Failure: Error: (error code)


12. RollKeySet in Demo
00 00 01 01
    ① ②
①：SelectApplication parameters, select application
②：RollKeyset parameters consisting: KeySetNo (1byte)
return value:
Success: Success
Failure: Error: (error code)


13. GetKeySettings in Demo
00 00 00
    ①
①       ：SelectApplication parameters, select application

                                               4
return value:
Success: 2 bytes
Failure: Error: (error code)


14. ChangeKeySettings in Demo
00 00 00 0F
    ①    ②
①       ：SelectApplication parameters, select application
② ：ChangeKeySettings Parameters, KeySettings (1byte)
return value:
Success: Success
Failure: Error: (error code)


15. GetKeyVersion in Demo
00 00 00 00 00
    ①       ②
①       ：SelectApplication parameters, select application
② ：GetKeyVersion parameters, Keyno (1 byte) + keysetno (1 byte)
return value:
Success: 1 bytes
Failure: Error: (error code)


16. CreateApplication in Demo
00 00 01 0F 91 01 00 10 10 00
                 ①
①：CreateApplication Parameters, Composition: AID (3 Bytes) + KeySett1 (1 Byte) + KeySett2 (1
Byte) + KeySett3 (1 Byte) + AKSVERSION (1 Byte) + NokeySets (1 Byte) + MaxKeysize (1 Byte) +
AppKeySetSett 1 byte)
return value:
Success: Success
Failure: Error: (error code)


17. CreateDelegatedApplication in Demo
00 00 02 00 00 00 10 00 0F 81 00 00 00 00 00
                ①
①：CreateDelegatedApplication Parameters, Composition: AID(3 bytes) + DAMSlotNo(2 bytes) +
DAMSlotVersion(1 byte) + QuotaLimit(2 bytes) + KeySett1(1 byte) + KeySett2(1 byte) +

                                               5
KeySett3(1 byte) + AKSVersion(1 byte) + NoKeySets(1 byte) + MaxKeySize(1 byte) +
AppKeySetSett(1 byte)
return value:
Success: Success
Failure: Error: (error code)


18. DeleteApplication in Demo
00 00 00 01 00
①       ②       ③
①：AuthKey certification number, default piccmasterkey 0x00
②：DeleteApplication parameters, Composition: AID (3 bytes)
③：Application certification type, default 0x00, EV3 is used, corresponding to ①
return value:
Success: Success
Failure: Error: (error code)


19. SelectApplication in Demo
00 00 01
    ①
① ：SelectApplication parameters, Composition: AID（3 bytes）
return value:
Success: Success
Failure: Error: (error code)


20. GetApplicationIDs in Demo
return value:
Success: n Bytes (n is determined by the application, one application 3 bytes, M app n = 3 * m
bytes)
Failure: Error: (error code)


21. GetDelegatedInfo in Demo
00 00
  ①
①：GetDelegatedInfo parameters, Composition: DAMSlotNo(2 bytes)
return value:
Success: 8 Bytes (1 Byte) + Quotalimit (2 Bytes) + FreeBlocks (2 Bytes) + AID (3 bytes))
Failure: Error: (error code)

                                                 6
22. CreateStdDataFile in Demo
Ev2 → 00 00 01 01 01 00 00 20 00 00
Ev3 → 00 00 01 01 01 00 00 20 00 00 07 41 00 00 20 00 00
   ①                ②                         ③
① ：SelectApplication parameters, select application
② ：CreateStdDataFile parameters, Composition: FileNo(1 byte) + FileOption(1 byte) +
   AccessRights(2 bytes + FileSize(3 bytes)
③ ：CreateStddatafile parameters, Composition: Fileno (1 Byte) + FileOption (1 Byte) +
   Accessrights (2 Bytes + FileSize (3 bytes), create a file, used for getFileCounters
   authentication (EV2 does not exist)
return value:
Success: Success
Failure: Error: (error code)


23. CreateBackupDataFile in Demo
00 00 01 02 01 00 00 20 00 00
   ①                ②
①：SelectApplication parameters, select application
②： CreateBackupDataFile parameters, Composition: FileNo(1 byte) + FileOption(1 byte) +
AccessRights(2 bytes + FileSize(3 bytes)
return value:
Success: Success
Failure: Error: (error code)



24. CreateValueFile in Demo
00 00 01 03 01 00 00 00 00 00 00 50 C3 00 00 C8 00 00 00 01
   ①                                 ②
①：SelectApplication parameters, select application
②：CreateValueFile parameters, Composition: FileNo(1 byte) + FileOption(1 byte) +
AccessRights(2 bytes) + LowerLimit(4 bytes) + UpperLimit(4 bytes) + Value(bytes) +
LimitedCreditEnabled(1 byte)
return value:
Success: Success
Failure: Error: (error code)




                                                  7
25. CreateLinearRecordFile in Demo
00 00 01 04 01 00 00 20 00 00 10 00 00
   ①                   ②
①：SelectApplication parameters, select application
②：CreateLinearRecordFile parameters, Composition: FileNo(1 byte) + FileOption(1 byte) +
AccessRights(2 bytes) + RecordSize(3 bytes) + MaxNoOfRecs(3 bytes)
return value:
Success: Success
Failure: Error: (error code)



26. CreateCyclicRecordFile in Demo
00 00 01 05 01 00 00 20 00 00 10 00 00
   ①                   ②
①：SelectApplication parameters, select application
②：CreateCyclicRecordFile parameters, Composition: FileNo(1 byte) + FileOption(1 byte) +
AccessRights(2 bytes) + RecordSize(3 bytes) + MaxNoOfRecs(3 bytes)
return value:
Success: Success
Failure: Error: (error code)



27. CreateTransactionMACFile in Demo
00 00 01 06 03 0F FF 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
   ①                                        ②
①：SelectApplication parameters, select application
②：CreateTransactionMACFile parameters, Composition: FileNo(1 byte) + FileOption(1 byte) +
AccessRights(2 bytes) + TMKeyOption(1 byte) + TMKey(16 bytes) + TMKeyVer(1 byte)
return value:
Success: Success
Failure: Error: (error code)


28. DeleteFile in Demo
00 00 01 05
   ①    ②
①：SelectApplication parameters, select application
②：DeleteFile parameters, Composition: FileNo(1 byte)

                                                8
return value:
Success: Success
Failure: Error: (error code)


29. GetFileIDs in Demo
00 00 01
   ①
①：SelectApplication parameters, select application
return value:
Success: n bytes (n is determined by the number of files existing in this app, one file 1 bytes, n
file n bytes)
Failure: Error: (error code)


30. GetFileSettings in Demo
00 00 01 01
   ①       ②
①：SelectApplication parameters, select application
②：GetFileSettings parameters, Composition:FileNo(1 byte)
return value:
Success: n bytes (n is determined by the file type)
Failure: Error: (error code)


31. ChangeFileSettings in Demo
Ev2→ 00 00 01 01 01 00 00
Ev3→ 00 00 01 07 43 00 00 41 FE FF
   ①                   ②
①：SelectApplication parameters, select application
②：ChangefileSettings Parameters, EV2 Composition: Fileno (1 Byte) + FileOption (1 Byte) +
Accessrights (2 Bytes), EV3 Composition: Fileno (1 Byte) + FileOption (1 Byte) + Accessrights (2
Bytes) + SDMOption (1 Byte + SDMACCESSRIGHTS (2 Bytes)
return value:
Success: Success
Failure: Error: (error code)


32. GetFileCounters(only on Ev3) in Demo
00 00 01 07
   ①       ②

                                                   9
①：SelectApplication parameters, select application
②：GetFileCounters parameters, Composition:FileNo(1 byte)
return value:
Success: 5 Bytes (SDMReadctr (3 bytes) + reserved (2 bytes))
Failure: Error: (error code)


33. ReadData in Demo
00 00 01 01 01 00 00 00 15 00 00
     ①                ②
①：SelectApplication parameters, select application
②：ReadData parameters, Composition:CommMode(1 byte) + FileNo(1 byte) + Offset(3 bytes) +
Length(3 bytes)
return value:
Success: n bytes (N is determined by the input parameter length, the value of Length is equal to
N)
Failure: Error: (error code)


34. WriteData in Demo
00 00 01 01 01 00 00 00 09 00 00 01 02 03 04 05 06 07 08 09
     ①                               ②
①：SelectApplication parameters, select application
②：WriteData parameters, Composition: CommMode (1 Byte) + Fileno (1 Byte) + LENGTH (3
Bytes) + Data (consistent with Length)
return value:
Success: Success
Failure: Error: (error code)


35. GetValue in Demo
00 00 01 01 03
     ①      ②
①：SelectApplication parameters, select application
②：GetValue parameters, Composition:CommMode(1 byte) + FileNo(1 byte)
return value:
Success: 4 bytes
Failure: Error: (error code)


36. Credit in Demo

                                                 10
00 00 01 01 03 02 00 00 00
   ①              ②
①：SelectApplication parameters, select application
②：Credit parameters, Composition:CommMode(1 byte) + FileNo(1 byte) + Data(4 bytes)
return value:
Success: Success
Failure: Error: (error code)


37. Debit in Demo
00 00 01 01 03 02 00 00 00
   ①              ②
①：SelectApplication parameters, select application
②：Debit parameters, Composition:CommMode(1 byte) + FileNo(1 byte) + Data(4 bytes)
return value:
Success: Success
Failure: Error: (error code)




38. LimitedCredit in Demo
00 00 01 01 03 02 00 00 00
   ①              ②
①：SelectApplication parameters, select application
②：LimitedCredit parameters, Composition:CommMode(1 byte) + FileNo(1 byte) + Data(4 bytes)
return value:
Success: Success
Failure: Error: (error code)


39. ReadRecords in Demo
00 00 01 01 04 00 00 00 01 00 00
   ①                  ②
①：SelectApplication parameters, select application
②：ReadRecords parameters, Composition:CommMode(1 byte) + FileNo(1 byte) + RecNo(3
bytes) + RecCount(3 bytes)
return value:
Success: n bytes (n is determined by the record size set when the loop file is created, namely n =
recordsize)
Failure: Error: (error code)


                                                 11
40. WriteRecord in Demo
00 00 01 01 04 00 00 00 01 00 00 01
     ①                 ②
①：SelectApplication parameters, select application
②：WriteRecord's parameters, Composition: CommMode (1 Byte) + Fileno (1 Byte) + LENGTH (3
bytes) + Data (consistent with Length)
return value:
Success: Success
Failure: Error: (error code)


41. UpdateRecord in Demo
00 00 01 01 04 00 00 00 00 00 00 01 00 00 01
①                        ②
①：SelectApplication parameters, select application
②：UpdateRecord Parameters, Composition: CommMode (1 Byte) + Fileno (1 Byte) + Recno (3
Bytes) + LENGTH (3 Bytes) + Data (consistent with Length)
return value:
Success: Success
Failure: Error: (error code)


42. ClearRecordFile in Demo
00 00 01 04
     ①   ②
①：SelectApplication parameters, select application
②：ClearRecordfile parameters, Fileno (1 byte)
return value:
Success: Success
Failure: Error: (error code)


43. CommitTransaction in Demo
01
①
①：CommitTransaction Parameters, Composition: Option(1 byte)
return value:
Success: 12 Bytes (TMC (4 Bytes) + TMV (8 bytes))
Failure: Error: (error code)


                                                12
44. AbortTransaction in Demo
return value:
Success: Success
Failure: Error: (error code)




45. CommitReaderID in Demo
00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
                         ①
①：CommitReaderID Parameters, Composition:TMRI（16 bytes）
return value:
Success: 16 Bytes (EnctMri)
Failure: Error: (error code)


46. Read_Sig in Demo
return value:
Success: 56 Bytes
Failure: Error: (error code)

6. Android API interface
Note: Byte sorting default small-end mode


1. int NFC_Device_Open();
    Function: Open NFC
    Return value: 0 →success
    Non-zero→ failure


2. int NFC_Device_Close();
    Function: Turn off    NFC
    Return value: 0 →success
    Non-zero → failure


3. int NFC_Device_Activate(byte[] timeoutMs, byte[] outData, int[]

    outLen);
    Function: Search and activate cards

                                              13
   Parameter: timeoutMs → Timeout 4 byte small end mode
   EXAMPLE: 0x10 0x00 0x00 0x00
   Established parameters: outData → receives data
   outLen →Length of outData
   Return value: 0→ success
   Non-zero→ failure


4. int NFC_Device_Remove();
   Function: Remove the card
   Return value: 0→ success
   Non-zero → failure


5. int CPU_GET_ATS(byte[] outData, int[] outLen);
   Function: Get Card ATS
   Parameter: no
   Established parameters: outData→ receives data
   outlen→Length of outData
   Return:value: 0→ success
   Non-zero→ failure


6. int AuthenticateISO(byte KeyNo, byte KeyType, byte[] Key);
   Function: EV1 card certification, used to activate EV2 / EV3 card
   Parameter: KeyNo → 1 byte default 0x00
   KeyType→1 byte default 0x00
   Key→16 byte default is 0x00
   Outlet parameters: no
   Return value: 0→ success
   Non-zero→ failure


7. int AuthenticateEV2First(byte[] Key,byte KeyNo,byte LenCap);
   Function: EV2 / EV3 card certification
   Parameter: KEY→ 16 byte authentication, white card default is 0x00
   KeyNo→ 1 byte




                                                14
   LenCap →1 byte default is 0x00
   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


8. int AuthenticateEV2First(byte[] Key,byte KeyNo,byte

   LenCap,byte[]PcdCap2);
   Function: EV2 / EV3 card certification
   Parameter: KEY →1616 byte authentication, white card default is 0x00
   KeyNo→16 1 byte




   LenCap →1 byte is not 0x00 value



   PcdCap2→1 to 6 bytes



   Outlet parameters: no
   Return value: 0→ success
   Non-zero→ failure



                                              15
9. int AuthenticateEV2NonFirst(byte[] Key,byte KeyNo);
   Function: EV2 / EV3 card certification
   Parameter: KEY → 16 byte authentication, white card default is 0x00
   KeyNo → 1 byte




   Outlet parameters: no
   Return value: 0 →success
   Non-zero →failure


10. int FreeMem(byte[] outData,int[] outLen) ;
   Function: Get free memory
   Parameter: no
   Established parameters: outData →receives data
   outLen →Length of outData
   Return value: 0 →success
   Non-zero→ failure


11. int Format();
   Function: format
   Return value: 0→ success
   Non-zero→ failure


12. int SetConfiguration(byte Option,byte[] Data);
   Function: Configuring card properties




                                              16
   Parameter: Option1 byte




            Data→1-25bytes




   Outlet parameters: no
   Return value: 0→ success
   Non-zero→ failure


13. int GetVersion(byte[] outData,int[] outLen);
   Function: Get the card version information
   Parameter: no
   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


14. int GetCardUID(byte[] outData,int[] outLen);
   Function: Get the card UID
   Parameter: no
   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→success
   Non-zero→ failure


15. int ChangeKeyEv1(byte KeyNo, byte[] Key);
   Function: EV1 card modified the secret key for activating EV2 / EV3
   Parameter: Keyno1 Byte




                                                17
   Key→116 byte The key after modification, it is set, do not understand, set all 0x00
   Outlet parameters: no
   Return value: 0→1 success
   Non-zero→1 failure


16. int ChangeKey(byte isEqualGetKeyNo,byte KeyNo,byte KeyVer,byte[]

   NewKey,byte[] OldKey);
            Function: Modify the secret key, or enable other secret keys
            Parameter: isEqualGetKeyNo→1 Byte 0x01 or 0x00
            Set 0x01 if the certified secret is the same as the target secret key
            Set 0x00 if the authentication secret is different from the key number of the target
            For example, AuthenticateEV2First or AuthenticateEV2NonFirst's Key number is the
       same as Key number used by ChangeKey, isEqualGetKeyNo is 0x01, the opposite 0x00
            KeyNo→1 byte




                                                18
   KeyVer→ byte is used for new secrets of new secrets, set it it yourself, don't understand
0x00
   NewKey→16 bytes, new secret keys, self-setting, do not understand 0x00
   OldKey→16 bytes, old secret keys, default all 0x00
   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


17. int ChangeKeyEV2(byte isEqualGetKeyNo,byte KeySetNo,byte

   KeyNo,byte KeyVer,byte[] NewKey,byte[] OldKey);
            Function: Modify the secret key, or enable other secret keys
            Parameter: isEqualGetKeyNo→1 Byte 0x01 or 0x00
            Set 0x01 if the certified secret is the same as the target secret key
            Set 0x00 if the authentication secret is different from the key number of the target
            For example, AuthenticateEV2First or AuthenticateEV2NonFirst's Key numberis the
       same as Key number used by ChangeKey, isEqualGetKeyNo is 0x01, the opposite 0x00
            KeySetNo→1 bytes




                                                19
            KeyNo→1 bytes




   KeyVer→1 byte is used for new secret keys, set up, set 0x00 if you don't understand
   NewKey→16bytes, new secret keys, settled, set 0x00 if you don't understand
   OldKey→16 bytes, old secret keys, default all 0x00
   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


18. int InitializeKeySet(byte KeySetNo,byte KeySetType);
   Function: Initialization Secret Key Set
     Parameter: KeySetNo→1 byte
   KeySetType→1 byte




                                               20
   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


19. int FinalizeKeySet(byte KeySetNo,byte KeySetVersion);
   Function: Complete the secret key set
     Parameter: KeySetNo→1 byte
   KeySetVersion→1 bytes




   Outlet parameters: no
   Return value: 0→ success
   Non-zero→ failure


20. int RollKeySet(byte KeySetNo);
   Function: Rolling Secret Key Set
    Parameter：KeySetNo→1byte
   Function: Rolling Secret Key Set
     Parameter: KeySetNo→1byte




                                           21
   Outlet parameters: no
   Return value: 0→ success
   Non-zero→failure


21. int GetKeySettings(byte[] outData,int[] outLen);
   Function: Get the secret key settings
   Parameter: no
   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero →failure


22. int ChangeKeySettings(byte KeySetting);
   Function: Change the key setting
   Parameter:KeySetting→1byte




   Outlet parameters: no
   Return value: 0→ success
   Non-zero →failure


23. int GetKeyVersion(byte KeyNo, byte KeySetNo, byte[] outData, int[]

   outLen);


   Function: Get the secret key version
   Parameter: KeyNo→1 bytes




                                            22
                       KeySetNo→1 byte




   Outlet parameters：outData→Receive data
      outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


24. int CreateApplication(byte[] AID, byte KeySett1, byte KeySett2, byte

   KeySett3, byte AKSVersion, byte NoKeySets, byte MaxKeySize, byte

   AppKeySetSett);
   Function: Create an app
   Parameter: AID →3 byte
   KeySett1→1 bytes
   KeySett2→1 bytes




                                            23
   KeySett3→1 byte
   AKSVersion →1 byte
   NoKeySets →1 byte
   MaxKeySize →1 byte
   AppKeySetSett →1 byte




Outlet parameters: no
Return value: 0 →success
Non-zero→ failure



                           24
25. int CreateDelegatedApplication(byte[] AID, byte[] DAMSlotNo, byte

   DAMSlotVersion, byte[] QuotaLimit, byte KeySett1, byte KeySett2,

   byte KeySett3, byte AKSVersion, byte NoKeySets, byte MaxKeySize,

   byte AppKeySetSett);
   Function: Create a delegated application
   Introducing parameters:AID →3bytes
           DAMSlotNo →2bytes
           DAMSlotVersion →1byte
           QuotaLimit →2bytes
      KeySett1→1byte
      KeySett2→1byte
      KeySett3→1byte
      AKSVersion →1byte




      NoKeySets →1byte
      MaxKeySize →1byte
      AppKeySetSett →1byte




   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure

                                              25
26. int DeleteApplication(byte[] AID, byte AppType);
   Function: Delete an application
   Introducing parameters:AID →3byte




                AppType→1byte
                If the authentication key is PICCMasterKey, AppType is set to 0x00 (applicable
            EV2 / EV3)
                If the authentication key is PICCDAMAuthKey, AppType is set to 0x10
            (Applicable EV3)
   Outlet parameters: no
   Return value: 0→ success
   Non-zero→ failure


27. int SelectApplication(byte[] AID);
   Function: Select an application
   Introducing parameters:AID→3bytes




   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


28. int SelectApplication(byte[] AID,byte[] AID2);
                Function: Select 2 applications
                Parameter: AID →3 byte
                AID2 →3 bytes




   Outlet parameters: no
   Return value: 0→ success
   Non-zero→ failure


29. int GetApplicationIDs(byte[] OutData, int[] OutLen);

                                                  26
   Function: Select Apply ID
   Parameter: no
   Established parameters:outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


30. int GetDelegatedInfo(byte[] DAMSlotNo, byte[] OutData, int[]

   OutLen);
   Function: Get Delegated application information
   Parameter: DAMSlotNo→2 byte



   Established parameters:outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


31. int CreateStdDataFile(byte FileNo,byte FileOption,byte[]

   AccessRights,byte[] FileSize);


   Function: Create a standard file
   Parameter: FileNo → 1byte




      FileOption →1byte
   Ev2：




   Ev3：

                                              27
      AccessRights→2byts



      FileSize →3bytes




   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


32. int CreateBackupDataFile(byte FileNo, byte FileOption, byte[]

   AccessRights, byte[] FileSize);
   Function: Create a backup file
   Parameter: FileNo →1byte




      FileOption →1byte




      AccessRights →2bytes

                                     28
      FileSize →3bytes




   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


33. int CreateValueFile(byte FileNo,byte FileOption,byte[]

   AccessRights,byte[] LowerLimit,byte[] UpperLimit,byte[] Value,byte

   LimitedCreditEnabled);
   Function: Create a value file
   Parameter: FileNo →1 byte




      FileOption →1byte




      AccessRights →2bytes



      LowerLimit →4bytes




      UpperLimit →4bytes




      Value →4bytes

                                     29
      LimitedCreditEnabled →1byte




   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


34. int CreateLinearRecordFile(byte FileNo, byte FileOption, byte[]

   AccessRights, byte[] RecordSize, byte[] MaxNoOfRecs);
   Function: Create a line record file
   Parameter: FileNo →1bytes
   Function: Create a linear record file
   Parameter: FileNo → 1 byte




      FileOption →1byte




      AccessRights →2bytes



      RecordSize →3bytes
      MaxNoOfRecs →3bytes


                                           30
   Outlet parameters: no
   Return value: 0→success
   Non-zero→ failure


35. int CreateCyclicRecordFile(byte FileNo, byte FileOption, byte[]

   AccessRights, byte[] RecordSize, byte[] MaxNoOfRecs);
   Function: Create a loop log file
   Parameter: FileNo →1byte
   Function: Create a loop log file
   Parameter: FileNo →1 byte




      FileOption →1byte




      AccessRights →2bytes



      RecordSize →3bytes
      MaxNoOfRecs →3bytes




   Outlet parameters: no
   Return value: 0→ success


                                      31
   Non-zero→ failure


36. int CreateTransactionMACFile(byte FileNo, byte FileOption, byte[]

   AccessRights, byte TMKeyOption, byte[] TMKey, byte TMKeyVer);
   Function: Create a TransactionMac file
   Parameter: FileNo →1 byte




      FileOption →1byte




      AccessRights →2bytes



      TMKeyOption →1bytes




      TMKey→16bytes
      TMKeyVer→1byte



   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


37. int DeleteFile(byte FileNo);
   Function: Delete a file
   Parameter: FileNo → 1 byte


                                            32
       Outlet parameters: no
       Return value: 0→ success
       Non-zero →failure


38. int GetFileIDs(byte[] OutData, int[] OutLen);
   Function: Get the ID of the file in the application
   Parameter: no
   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


39. int GetFileSettings(byte FileNo, byte[] outData, int[] outLen);
   Function: Get the configuration of the file
   Parameter: FileNo→ byte




   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


40. int GetFileCounters(byte FileNo, byte[] outData, int[] outLen);
   Function: Get the count count (EV3 proprietary)
   Parameter: FileNo→1byte




                                                  33
   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure




41. int ChangeFileSettings(byte FileNo, byte FileOption, byte[]

   AccessRights);


   Function: Change the configuration of the file
   Parameter:FileNo→1 byte




      FileOption →1byte




      AccessRights →2byte


                                                34
   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


42. int ChangeFileSettings(byte FileNo, byte FileOption, byte[]

   AccessRights, byte[] Option);
   Function: Change the configuration of the file
   Parameter: FileNo→1byte




      FileOption →1byte




      AccessRights →21bytes



      Option→Optional parameters, please decide according to the parameter description, the
   following is only partial screenshots




                                                35
   Outlet parameters: no
   Return value: 0→ success
   Non-zero→ failure


43. int ReadData(byte FileNo, byte[] Offset, byte[] Length, byte

   CommMode, byte[] outData, int[] outLen);
   Function: Read standard files or backup file data
   Parameter: FileNo→1 byte




      Offset →3bytes



      Length →3bytes




                                               36
      CommMode→1byte




   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


44. int WriteData(byte FileNo,byte[] Offset,byte[] Length,byte

   CommMode,byte[] Data);


   Function: Write a standard file or backup file data
   Parameter: FileNo→1byte




      Offset→3bytes



      Length →3bytes




      CommMode→1byte




      Data→ Length byte
                                                 37
   Outlet parameters: no
   Return value: 0 →success
   Non-zero→ failure


45. int GetValue(byte FileNo, byte CommMode, byte[] outData, int[]

   outLen);
   Function: Get the value of the value file
   Parameter: FileNo→1 byte




      CommMode→1byte




   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


46. int Credit(byte FileNo,byte CommMode,byte[] Data);
   Function: Add value of value files


   Parameter:FileNo→1 byte




      CommMode→1 byte



                                               38
      Data→4bytes



   Parameter: no
   Return value: 0→ success
   Non-zero→ failure


47. int LimitedCredit(byte FileNo, byte CommMode, byte[] Data);
   Function: Limit the value of added value files
   Parameter: FileNo→1byte




      CommMode→1byte




      Data→4bytes



   Parameter: no
   return value:0 →success
      Non-zero →fail


48. int Debit(byte FileNo,byte CommMode,byte[] Data);
   Function: Reduce the value of the value file
   Parameter：FileNo→1byte


                                                  39
      CommMode→1byte




      Data→4bytes



   Parameter: no
   return value:0 →success
      Non-zero →fail


49. int ReadRecords(byte FileNo, byte[] RecNo, byte[] RecCount, byte

   CommMode, byte[] outData, int[] outLen);
   Function: read the data of the record file
   Parameter：FileNo→1byte




      RecNo →3bytes




      RecCount →3bytes




                                                40
      CommMode→1byte




   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure




50. int WriteRecord(byte FileNo,byte[] Offset,byte[] Length,byte

   CommMode,byte[] Data);
   Function: Write data to record files
   Parameter：FileNo→1byte




      Offset →3bytes



      Length →3bytes




      CommMode→1byte




                                            41
      Data→ Length byte


   Parameter: no
   return value:0 →success
      Non-zero →fail


51. int UpdateRecord(byte FileNo,byte[] RecNo,byte[] Offset,byte[]

   Length,byte CommMode,byte[] Data);
   Function: Update the data of the record file
   Parameter：FileNo→1byte




      RecNo→3bytes




      Offset →3bytes
      Length →3bytes




      CommMode→1byte




                                                  42
      Data→ Length byte


   Parameter: no
   return value:0 →success
      Non-zero →fail


52. int ClearRecordFile(byte FileNo);
   Function: Clear the record file
   Parameter：FileNo→1byte




   Parameter: no
   return value:0 →success
      Non-zero →fail


53. int CommitTransaction();
   Function: Submit a transaction
   Parameter：no
   return value:0 →success
      Non-zero →fail


54. int CommitTransaction(byte Option, byte[] outData, int[] outLen);
   Function: Submit a transaction
   Parameter：Option→1 byte




                                        43
   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


55. int AbortTransaction();
   Function: Interrupt Transaction
   Parameter：no
   Parameter: no
   return value:0 →success
      Non-zero →fail


56. int CommitReaderID(byte[] TMRI, byte[] outData, int[] outLen);
   Function: Submit Reader ID
   Parameter：TMRI→16bytes


   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure


57. int Read_Sig(byte Address, byte[] outData, int[] outLen);
   Function: Retrieve the ECC original verification signature
   Parameter：Address →1byte




   Established parameters: outData→ receives data
   outLen→Length of outData
   Return value: 0→ success
   Non-zero→ failure




                                                44
7. Demo Test process

  Ev2/Ev3 Demo Test
         Step




      Open NFC




    Check IC Card




      ……
    （一条一条往
     下执行）




                      CommitReaderI     CommitTransact
        Debit                                             LimitedCredit
                           D                ion




                      CommitTransact    CommitReaderI
     ReadRecord                                           WriteRecord
                          ion                D




                                        CommitReaderI    CommitTransact
    UpdateRecord      ClearRecordFile
                                             D               ion




                      DeleteApplicati                    AbortTransactio
       Format                              Read_Sig
                            on                                  n




     Close_NFC




                                             45

---

## NFC_cpu_card_api

          CPU Card API
Development Documentation
                     V1.0.0
             Update Records
  Date                    Description

2024-07-04            First Edition Release
                  Contents
1 Introduction
   1.1 Open
   1.2 Close
   1.3 Activate
   1.4 Get ATS
   1.5 Transmit
   1.6 Halt
   1.7 Remove
1 Introduction
The NFC interface is deﬁned under the com.tps550.api. And the CPU card interface of the operationclass
nfc is deﬁned as follows:

1.1 Open

   /**
    * @Function Open the NFC device
    *
    * @Exception CommonException
    * 1.DeviceAlreadyOpenException: The device has been turned on
    * 2.DeviceNotFoundException: The device cannot be opened
    *
    * */

   public synchronized void open() throws CommonException



1.2 Close

   /**
    * @Function Close the NFC device
    *
    * @Exception CommonException
    *
    * */

   public synchronized void close()throws CommonException



1.3 Activate

   /**
    * @Function NFC card selection and card reading basic information
    *
    * @Parameter
    * 1.timeOut: Timeout, ms
    *
    * @Exception CommonException
    * 1.IllegalArgumentException: data is invalid
    * 2.DeviceNotOpenException: The device cannot be opened
    * 3.TimeoutException: overtime
    *
    * @Return byte[]: In the form of byte array byte[] returns card, type, data card
   type, card number and other card basic information data
    *
   * */

  public synchronized byte[] activate(int timeOut) throws CommonException



1.4 Get ATS

  /**
   * @Function Get the ATS data for the card
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotFoundException: The device cannot be opened
   *
   * @Return byte[]: returns the ATS data of the card in the form of byte array
  byte[]
   *
   * */

  public synchronized byte[] cpu_get_ats() throws CommonException



1.5 Transmit

  /**
   * @Function NFC data sending
   *
   * @Parameter
   * 1.sendBuffer: Byte array to send
   * 2.sendBufferLength: Data byte array length to send
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: Data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */

  public synchronized byte[] transmit(byte[] sendBuffer, int sendBufferLength)
  throws CommonException



1.6 Halt

  /**
   * @Function Hang up the card
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotFoundException: The device cannot be opened
   *
   * */

  public synchronized void halt() throws CommonException



1.7 Remove

  /**
   * @Function Waiting for card removal
   *
   * @Parameter
   * 1.timeOut: Timeout
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotFoundException: The device cannot be opened
   *
   * */

  public synchronized void remove(int timeOut) throws CommonException

---

## NFC_desfire_ev1_card_api

  Desﬁre EV1 Card API
Development Documentation
                     V1.0.0
             Update Records
  Date                    Description

2024-07-04            First Edition Release
                                          Contents
1 NFC Class
2 Password Authentication
3 Modify the key
4 Create an App
5 Create a File
6 Delete existing App
7 Delete an existing File
8 Format the card
9 Get the APPID of all existing APPs
10 Get the FileNo of all existing files
11 Select the corresponding APPLICATION by APPID
12 Read data from the target file
13 Write data to the target file
1 NFC Class

 /**
  * @Function NFC Construction method.
  *
  * @Parameter
  * 1.context: Context
  *
  * @Return Nfc: Nfc object
  *
  * */

 public Nfc(Context context)




2 Password Authentication

 /**
  * @Function Password authentication. You need to select a directory before
 calling this method.
  *
  * @Parameter
  * 1.KeyNo: The number of the Key. 1 byte, KeyNo = 0 means master key.
  * 2.keytype: The type of key. 1 byte, for expansion, currently needs to be set to
 0.
  * 3.desKey: The key. 16 bytes, The key which encryption and decryption uses a 16-
 byte 2K3DES algorithm. The default password for the new card is 16 bytes of 0x00.
  *
  * @Return 0, success; Non 0, failed
  *
  * */

 public synchronized int md_auth(byte KeyNo, byte keytype, byte[] desKey) throws
 CommonException




3 Modify the key

 /**
  * @Function Modify the key. Password authentication is required before calling
 this method.
  *
  * @Parameter
  * 1.KeyNo: The number of the target key. 1 byte.
  * 2.desKey: The new key.16 bytes.
  *
  * @Return 0, success; Non 0, failed
  *
  * */

 public synchronized int md_change_key(byte KeyNo, byte[] desKey) throws
 CommonException




4 Create an App

 /**
  * @Function Create an App. You must first select the system directory before
 calling this method.
  *
  * @Parameter
  * 1.appid: Each APPLICATION needs to be associated with an APPID. 4 bytes, only
 the low 24bit is valid, that is, the maximum value is 0xFFFFFF.
  * 2.KeySetting: The permission settings of key.1 byte,and the defult value is
 0x0F.
  * 3.MaxKeyNo: The maximum number of Keys that the APPLICATION can contain.1 byte.
  *
  * @Return 0, success; Non 0, failed
  *
  * */

 public synchronized int md_create_app(int appid, byte KeySetting, byte MaxKeyNo)
 throws CommonException




5 Create a File

 /**
  * @Function Create a File.You need to select an existing target directory Before
 calling this method.
  *
  * @Parameter
  * 1.FileNo: Each file needs to be associated with a FileNo. 1 byte, the maximum
 number of files that can be created in each APPLICATION is 16, so the value range
 is 0x00-0x0F.
  * 2.commset: Communication encryption. 1 byte, currently only supports commset =
 0 for MACed encrypted communication.
  * 3.inBuf: Indicates which keys the file needs to verify for read and write
 permissions. 2 bytes, please refer to Desfire's specification for specific
 meaning. If it is not clear, it can be set to 0x0000.
  * 4.inLen: The byte array length of inBuf.
  * 5.filesize: The size of the file created. 4 bytes, only low 24bit effective.
  *
  * @Return 0, success; Non 0, failed
  *
  * */

 public synchronized int md_create_std_data_file(byte FileNo, byte commset, byte[]
 inBuf, int inLen, int filesize) throws CommonException




6 Delete existing App

 /**
  * @Function Delete existing App.You need to select the system directory and pass
 password authentication before calling this method.
  *
  * @Parameter
  * 1.appid: APPID corresponding to the target App.
  *
  * @Return 0, success; Non 0, failed
  *
  * */

 public synchronized int md_del_app(int appid) throws CommonException




7 Delete an existing File

 /**
  * @Function Delete an existing File. You need to select the target directory and
 pass password authentication before calling this method.
  *
  * @Parameter
  * 1.FileNo: FileNo corresponding to the target file
  *
  * @Return 0, success; Non 0, failed
  *
  * */

 public synchronized int md_del_file(byte FileNo) throws CommonException




8 Format the card

 /**
  * @Function Format the card.You need to select the system directory and pass
 password authentication before calling this method.
   *
   * @Return 0, success; Non 0, failed
   *
   * */

  public synchronized int md_factory_reset() throws CommonException




9 Get the APPID of all existing APPs

  /**
   * @Function Get the APPID of all existing APPLICATIONs.You must first select the
  system directory before calling this method.
   *
   * @Return Byte array containing all APPIDs
   *
   * */

  public synchronized byte[] md_get_app_ids() throws CommonException




10 Get the FileNo of all existing ﬁles

  /**
   * @Function Get the FileNo of all existing files.You need to select an existing
  target directory Before calling this method.
   *
   * @Return Byte array containing all FileNos
   *
   * */

  public synchronized byte[] md_get_file_ids() throws CommonException




11 Select the corresponding APPLICATION by APPID

  /**
   * @Function Select the corresponding APPLICATION by APPID. When performing
  directory level related operations, such as creating a directory, you must first
  select the directory with APPID = 0. This directory is the default system
  directory and cannot be deleted or created.
   *
   * @Parameter
   * 1.appid: APPID corresponding to the target APPLICATION.
   *
  * @Return 0, success; Non 0, failed
  *
  * */

 public synchronized int md_select_app(int appid) throws CommonException




12 Read data from the target ﬁle

 /**
  * @Function Read data from the target file.You need to select the target
 directory and pass password authentication before calling this method.
  *
  * @Parameter
  * 1.FileNo: FileNo corresponding to the target file.
  * 2.offset: Read the offset of the position. 4 bytes, only low 24bit effective.
  * 3.inLen: Read the length of the data.4 bytes, only low 24bit effective.
  *
  * @Return The data content read. No more than 256 bytes
  *
  * */

 public synchronized byte[] md_read_data(byte FileNo, int offset, int inLen) throws
 CommonException




13 Write data to the target ﬁle

 /**
  * @Function Write data to the target file.You need to select the target directory
 and pass password authentication before calling this method.
  *
  * @Parameter
  * 1.FileNo: FileNo corresponding to the target file.
  * 2.offset: The offset of the write location.4 bytes, only low 24bit effective.
  * 3.length: The length of the data written.4 bytes, only low 24bit effective.
  * 4.inBuf: The data content written. No more than 256 bytes
  * 5.inLen: The byte array length of inBuf.
  *
  * @Return 0, success; Non 0, failed
  *
  * */

 public synchronized int md_write_data(byte FileNo, int offset, int length, byte[]
 inBuf, int inLen) throws CommonException

---

## NFC_felica_card_api

        Felica Card API
Development Documentation
                     V1.0.0
             Update Records
  Date                    Description

2024-07-04            First Edition Release
                                     Contents
1 Command return value description
2 Error code description
3 Operating instructions
4 Operation analysis
   4.1 Polling
   4.2 Read
   4.3 Write
5 Android API
   5.1 Open
   5.2 Close
   5.3 Card searching
   5.4 Read data
   5.5 Write data
6 Reference documents
7 Demo test process
1 Command return value description
Success: no data: success (PS: return value is 0)
With data: relevant command demand data (e.g. read)
Failed: error: (error code)


2 Error code description
Negative error code: (mostly loss of communication between PICC and PCD, poor contact, re check the card)


              Error Code                                    Description

                   -1            Error in parsing input parameters of single chip microcomputer

                   -2                       APK layer interface input parameter error

                  -10                               APK layer transmission error

                 -1001                                    Parameter error

                 -1002                                        IO error

                 -1003                                     timeout error

                 -1004                                      Check error

                 -1005                                     framing error

                 -1006                                   Interference error

                 -1007                                     Protocol error

                 -1008                                  Card conflict error

                 -1009                                      Send error



3 Operating instructions
In our demo, you can test it as follow:

           1. Click "open NFC" to open NFC and place the card in the identification area
           2. Click "polling" to poll the card. If there is a card, you can continue to execute "read" and "write"


4 Operation analysis
Note: the input parameter is hexadecimal, and the return value with data output is hexadecimal

4.1 Polling
FF FF 00 03
Yellow area: System Code
Green area: Request Code
Blue area: Time Slot Number

Return Value
Success: length (1 byte) + response code (1 byte) + IDM (8 bytes) + pad (8 bytes)
Failed: error: (error code)


4.2 Read
01 09 00 01 80 00
Yellow area: Number of Services
Green area: Service Code List
Blue area: Number of Blocks
Purple area: Block List

Return Value
Success: Length (1 byte) + response code (1 byte) + IDM (8 bytes) + Status ﬂag1 (1 byte)+ Status Flag2 (1
byte) + Number of blocks (1 byte) + block data (16 * m bytes (M is the value of number of blocks))
Failed: Error: (error code)


4.3 Write
01 09 00 01 80 00 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F

Yellow area: Number of Services
Green area: Service Code List
Blue area: Number of Blocks
Purple area: Block List
Grey area: Block Data

Return Value
Success: success
Failed: error: (error code)


5 Android API
Package:com.common.apiutil.nfc.Nfc

5.1 Open

    /**
     * @Function Open the NFC device
     *
     * @Exception CommonException
     * 1.DeviceAlreadyOpenException: The device has been turned on
     * 2.DeviceNotFoundException: The device cannot be opened
   *
   * */

  public synchronized void open() throws CommonException



5.2 Close

  /**
   * @Function Close the NFC device
   *
   * @Exception CommonException
   *
   * */

  public synchronized void close()throws CommonException



5.3 Card searching

  /**
   * @Function Card searching
   *
   * @Parameter
   * 1.cmd: cmd->4 bytes; System code/Reserved/Time slot number
   * 2.cmdLen: cmd length
   *
   * @Return Success, card data; Failed, CommonException
   *
   * */

  public synchronized byte[] FelicaPolling(byte[] cmd, int cmdLen) throws
  CommonException



             System code              Reserved         Time slot number

                2 bytes               1 byte '00'           1 byte


5.4 Read data

  /**
   * @Function Read data
   *
   * @Parameter
   * 1.cmd: cmd->determines the number of bytes according to the set parameter;
  Number of Services/Service Code List/Number of Blocks/Block List
   * 2.cmdLen: cmd length
   *
   * @Return Success, card data; Failed, CommonException
   *
   * */

  public synchronized byte[] FelicaRead(byte[] cmd, int cmdLen) throws
  CommonException



         Command      bytes   Multiple   Description

                                         It depends on the implementation of the Type 3
         Number of
                      1       n          Tag how many Services can be read
         Services
                                         simultaneously.

         Service                         Each Service Code is specified in Little Endian
                      2n
         Code List                       format.

                                         It depends on the implementation of the Type 3
         Number of
                      1       m          Tag how many blocks can be read
         Blocks
                                         simultaneously.

                      2m-
         Block List                      See Section 5.6.
                      3m


5.5 Write data

  /**
   * @Function Write data
   *
   * @Parameter
   * 1.cmd: cmd->determines the number of bytes according to the set parameter;
  Number of Services/Service Code List/Number of Blocks/Block List/Block Data
   * 2.cmdLen: cmd length
   *
   * @Return Success, card data; Failed, CommonException
   *
   * */

  public synchronized byte[] FelicaWrite(byte[] cmd, int cmdLen) throws
  CommonException



         Command      bytes   Multiple   Description

                                         It depends on the implementation of the Type 3
         Number of
                      1       n          Tag how many Services can be written
         Services
                                         simultaneously.

         Service                         Each Service Code is specified in Little Endian
                      2n
         Code List                       format.
            Command       bytes   Multiple   Description

                                             It depends on the implementation of the Type 3
            Number of
                          1       m          Tag how many blocks can be written
            Blocks
                                             simultaneously.

                          2m-
            Block List                       See Section 5.6.
                          3m

            Block Data    16m



6 Reference documents
Please refer these online document:

<Type 3 Tag Operation Speciﬁcation.pdf>
<JIS-X-6319-4-2005.pdf>


7 Demo test process
          1. Open NFC
          2. Polling
          3. Read
          4. Write
          5. Close NFC

---

## NFC_mifare_classic_card_api

Mifare Classic Card API
Development Documentation
                     V1.0.0
             Update Records
  Date                    Description

2024-07-04            First Edition Release
                       Contents
1 Introduction
   1.1 Open
   1.2 Close
   1.3 Activate
   1.4 Read block
   1.5 Write block
   1.6 Read value
   1.7 Write value
   1.8 Increment
   1.9 Decrement
   1.10 Authenticate
   1.11 Halt
   1.12 Remove
1 Introduction
The Mifare Classic Card interface of the operation class NFC is deﬁned as follows:


1.1 Open

   /**
    * @Function Open the NFC device
    *
    * @Exception CommonException
    * 1.DeviceAlreadyOpenException: The device has been turned on
    * 2.DeviceNotFoundException: The device cannot be opened
    *
    * */

   public synchronized void open() throws CommonException



1.2 Close

   /**
    * @Function Close the NFC device
    *
    * @Exception CommonException
    *
    * */

   public synchronized void close() throws CommonException



1.3 Activate

   /**
    * @Function NFC card selection and card reading basic information
    *
    * @Parameter
    * 1.timeOut: Timeout, ms
    *
    * @Exception CommonException
    * 1.IllegalArgumentException: data is invalid
    * 2.DeviceNotOpenException: The device cannot be opened
    * 3.TimeoutException: Timeout
    *
    * @Return byte[]
    *
    * */
  public synchronized byte[] activate(int timeOut) throws CommonException



1.4 Read block

  /**
   * @Function NFC M1 card reads the block data
   *
   * @Parameter
   * 1.noBlock: The block area code (0-63) to read is represented in bytes, as the
  second block of the first sector is represented as 1.
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */

  public synchronized byte[] m1_read_block(byte noBlock) throws CommonException



1.5 Write block

  /**
   * @Function NFC M1 card writes data to the card block area
   *
   * @Parameter
   * 1.noBlock: The block area code (0-63) to read is represented in bytes, as the
  second block of the first sector is represented as 1.
   * 2.inBuf: The data that needs to be written to the card is represented in bytes,
  16 bytes in length
   * 3.inLen: The length of the data that needs to be written to the card
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */

  public synchronized void m1_write_block(byte noBlock, byte[] inBuf, int inLen)
  throws CommonException



1.6 Read value

  /**
   * @Function Read the value to the M1 card
   *
   * @Parameter
   * 1.noBlock: The block area code (0-63) to read is represented in bytes, as the
  second block of the first sector is represented as 1.
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */

  public synchronized byte[] m1_read_value(byte noBlock) throws CommonException



1.7 Write value

  /**
   * @Function Read the value to the M1 card
   *
   * @Parameter
   * 1.noBlock: The block area code (0-63) to read is represented in bytes, as the
  second block of the first sector is represented as 1.
   * 2.inBuf: The data that needs to be written to the card is represented in bytes,
  16 bytes in length
   * 3.inLen: The length of the data that needs to be written to the card
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */

  public synchronized void m1_write_value(byte noBlock, byte[] inBuf,int inLen)
  throws CommonException



1.8 Increment

  /**
   * @Function M1 card adds value (addition)
   *
   * @Parameter
   * 1.srcAddr: Source block address, 1 bytes
   * 2.destAddr: Target block address, 1 bytes
   * 3.inBuf: Values are represented in an array of bytes
   * 4.inLen: Length of byte array
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */
  public synchronized void m1_increment(byte srcAddr, byte destAddr, byte[] inBuf,
  int inLen) throws CommonException




1.9 Decrement

  /**
   * @Function M1 card value reduction (M1 card subtraction)
   *
   * @Parameter
   * 1.srcAddr: Source block address, 1 bytes
   * 2.destAddr: Target block address, 1 bytes
   * 3.inBuf: Values are represented in an array of bytes
   * 4.inLen: Length of byte array
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */

  public synchronized void m1_decrement(byte srcAddr, byte destAddr, byte[] inBuf,
  int inLen) throws CommonException



1.10 Authenticate

  /**
   * @Function Card sector password check
   *
   * @Parameter
   * 1.noBlock: Block size code (0-63) to verify
   * 2.passwdType: Type of password to check, keyA (0x0A) or keyB (0x0B), two types
   * 3.passwd: The password to be checked is represented as an array of bytes
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */

  public synchronized void m1_authenticate(byte noBlock, byte passwdType, byte[]
  passwd) throws CommonException



1.11 Halt
  /**
   * @Function Hang up the card
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */

  public synchronized void halt() throws CommonException



1.12 Remove

  /**
   * @Function Waiting for card removal
   *
   * @Parameter
   * 1.timeOut: Timeout, ms
   *
   * @Exception CommonException
   * 1.IllegalArgumentException: data is invalid
   * 2.DeviceNotOpenException: The device cannot be opened
   *
   * */

  public synchronized void remove(int timeOut) throws CommonException

---

## can_bus_api_en

           CAN Bus API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2022-10-31              First Edition Release

2023-02-22                Modify the API

2023-04-23                Update the API

2024-06-20         Update API and documentation
                                    Contents
1 Introduction
   1.1 Frame Format
   1.2 Frame Type
   1.3 CanRecvInfo Class
   1.4 CanUtil Class
     1.4.1 Open the CAN Bus
     1.4.2 Close the CAN Bus
     1.4.3 Get the Status of the CAN Bus
     1.4.4 Set the Bit Rate
     1.4.5 CAN Bus Receive Data
     1.4.6 CAN Bus Receive
     1.4.7 CAN Bus Send Frames
   1.5 CanUtil2 Class
     1.5.1 Constant
     1.5.2 Data frame
     1.5.3 Set Can working mode
     1.5.4 Can filter mode
     1.5.5 Send the Can message
     1.5.6 Read the Can message
1 Introduction
1.1 Frame Format

                                Frame Format                                Description

                     CanUtil.STANDARD_FRAME_FORMAT                        Standard Frame

                       CanUtil.EXTEND_FRAME_FORMAT                          Extend Frame


1.2 Frame Type

                                Frame Type                                 Description

                         CanUtil.DATA_FRAME_TYPE                            Data Frame

                        CanUtil.REMOTE_FRAME_TYPE                         Remote Frame


1.3 CanRecvInfo Class
Package: com.common.apiutil.can
CAN receives information classes and can set or get property values through the set and get methods.



   private String recvId;            // Receive ID
   private int recvDataLen;          // Data length
   private byte[] recvData;          // Data
   private int frameFormat;          // Frame format
   private int frameType;            // Frame type



1.4 CanUtil Class
1.4.1 Open the CAN Bus


   /**
    * @Function Open the CAN Bus
    *
    * @Parameter
    * 1.bitRate: Bit rate
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static int open(int bitRate)



1.4.2 Close the CAN Bus
   /**
    * @Function Close the CAN Bus
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static int close()



1.4.3 Get the Status of the CAN Bus


   /**
    * @Function Get the status of the CAN bus
    *
    * @Return 0, Close; 1, Open
    *
    * */

   public static int getStatus()



1.4.4 Set the Bit Rate


   /**
    * @Function Set the bit rate
    *
    * @Parameter
    * 1.bitRate: value
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static int setBitRate(int bitRate)



1.4.5 CAN Bus Receive Data


   /**
    * @Function CAN receive data
    *
    * @Parameter
    * 1.canRecvInfo: A data object that contains the received ID and data frame
    *
    * */

   public interface CanOperationListener
   {
        void canInput(CanRecvInfo canRecvInfo);
   }



1.4.6 CAN Bus Receive


   /**
    * @Function CAN bus receive
    *
    * @Parameter
    * 1.canOperationListener: Receive callback
    *
    * */

   public static void canRecv(CanOperationListener canOperationListener)



1.4.7 CAN Bus Send Frames

(standard frame/extended frame/data frame/remote frame)



   /**
    * @Function CAN Bus Send Frames
    *
    * @Parameter
    * 1.frameFormat:
    *      1.CanUtil.STANDARD_FRAME_FORMAT(standard frame)
    *      2.CanUtil.EXTEND_FRAME_FORMAT(extended frame)
    * 2.frameType:
    *      1.CanUtil.DATA_FRAME_TYPE(data frame)
    *      2.CanUtil.REMOTE_FRAME_TYPE(remote frame)
    * 3.id: ID Number
    * 4.data: Data Frame，Remote Frame(0x00)
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * @CommonException
    * Parameter exception, ID is not hexadecimal and starts at 0, or the frame format
   is not a standard frame or extended frame, or the frame type is not a data frame
   or a remote frame
    *
    * */

   public static int canSend(int frameFormat, int frameType, String id, byte[] data)
   throws CommonException



1.5 CanUtil2 Class
Package: com.common.apiutil.can.CanUtil2
Can Control class

1.5.1 Constant

                             Constant                    Value                Description

                    CanUtil2.CAN_ID_STANDARD               0              Standard ID Frame

                    CanUtil2.CAN_ID_EXTENDED               4              Extended ID Frame

                     CanUtil2.CAN_RTR_DATA                 0                  Data Frame

                    CanUtil2.CAN_RTR_REMOTE                2                Remote Frame


1.5.2 Data frame

(Take a frame of CAN data frame as an example)


                          Data bit                                  Description

              data[0], data[1], data[2], data[3]            4 bytes of the standard ID

              data[4], data[5], data[6], data[7]           4 bytes of the extension ID

                                                    1 byte frame type, indicating whether the
                           data[8]                    data frame is a standard frame or an
                                                                  extended frame

                                                   1 byte frame type, indicating whether it is a
                           data[9]
                                                          data frame or a remote frame

                                                   1 byte length, indicating the effective data
                          data[10]
                                                                 length in the frame

                data[11], data[12], data[13],
                data[14], data[15], data[16],                    8 bytes of data bits
                      data[17], data[18]

                          data[19]                                   1 byte FMI



Dataframe parsing example:



   CanUtil2 can = new CanUtil2(getApplicationContext());
   byte[] ret = can.read(20, 5);
   if (ret.length < 20) {
       return;
   }

   int id;

   if (ret[8] == CanUtil2.CAN_ID_STANDARD) {
       // Standard ID
       id = (((int) ret[3] & 0xff) << 24) + (((int) ret[2] & 0xff) << 16) +
           (((int) ret[1] & 0xff) << 8) + (((int) ret[0] & 0xff));
   } else if (ret[8] == CanUtil2.CAN_ID_EXTENDED) {
       // Extended ID
       id = (((int) ret[7] & 0xff) << 24) + (((int) ret[6] & 0xff) << 16) +
           (((int) ret[5] & 0xff) << 8) + (((int) ret[4] & 0xff));
   }

   if (ret[9] == CanUtil2.CAN_RTR_DATA) {
       // Data frame
       String dataString = new String(ret, 11, 8, StandardCharsets.UTF_8);
   } else if (ret[9] == CanUtil2.CAN_RTR_REMOTE) {
       // Remote frame
   }



1.5.3 Set Can working mode


   /**
    * @Function Configure CAN working mode
    *
    * @Parameter
    * 1.CAN_SJW: Resync skip width (SJW), Range: [0, 3]
    * 2.CAN_BS1: Time1, Range: [0, 15]
    * 3.CAN_BS2: Time2, Range: [0, 7]
    * 4.CAN_Prescaler: Range: [1, 1024]
    * 5.CAN_MODE: Normal mode or Loop mode
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int configMode(int CAN_SJW, int CAN_BS1, int CAN_BS2, int
   CAN_Prescaler, int CAN_MODE)




Baud rate calculation formula:
Baud Rate=108×1000000/"CAN_Prescaler"/("CAN_SJW"+"CAN_BS1"+"CAN_BS2"+3)

1.5.4 Can ﬁlter mode


   /**
    * @Function Configure CAN filter mode
    *
    * @Parameter
    * 1.CAN_FilterNumber: Filter number, Range: [0, 13]
    * 2.CAN_FilterMode: Mode, {@code 0} Shielded bit mode; {@code 1} Identifier list
   mode
   * 3.CAN_FilterScale: Filter Scale{@code 0} 16 bits;{@code 1} 32 bits
   * 4.CAN_FilterIdHigh: Used to store the ID to be filtered, Range: [0, 65535]
   * 5.CAN_FilterIdLow: Used to store the ID to be filtered, Range: [0, 65535]
   * 6.CAN_FilterMaskIdHigh: Used to store the ID or mask to filter, Range: [0,
  65535]
   * 7.CAN_FilterMaskIdLow: Used to store the ID or mask to filter, Range: [0,
  65535]
   * 8.CAN_FilterActivation: {@code 0} Close；{@code 1} Open
   *
   * @Return ResultCode: please refer the quick guidance document.
   *
   * */

  public synchronized int configFilter(
      int CAN_FilterNumber,
      int CAN_FilterMode,
      int CAN_FilterScale,
      int CAN_FilterIdHigh,
      int CAN_FilterIdLow,
      int CAN_FilterMaskIdHigh,
      int CAN_FilterMaskIdLow,
      int CAN_FilterActivation)



1.5.5 Send the Can message


  /**
   * @Function Write information to Can bus
   *
   * @Parameter
   * 1.ID: Message ID, Range:[0, 65535]
   * 2.IDE:
   *      1.CanUtil2.CAN_ID_STANDARD
   *      2.CanUtil2.CAN_ID_EXTENDED
   * 3.RTR:
   *      1.CanUtil2.CAN_RTR_DATA
   *      2.CanUtil2.CAN_RTR_REMOTE
   * 4.data: data value
   * 5.len: data length
   *
   * @Return ResultCode: please refer the quick guidance document.
   *
   * */

  public synchronized int write(int ID, int IDE, int RTR, byte[] data, int len)



1.5.6 Read the Can message


  /**
   * @Function Read data from Can bus
 *
 * @Parameter
 * 1.len: Read data length (recommended to read in multiples of 20)
 * 2.timeout: timeout waiting time
 *
 * @Return ResultCode: please refer the quick guidance document.
 *
 * */

public synchronized byte[] read(int len, int timeout)

---

## ic_psam_card_api_en

      IC/Psam Card API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2022-10-31              First Edition Release

2023-02-22              Modify the interface

2024-06-20         Update API and documentation
                                     Contents
1 Smart Card Reader
   1.1 IC description of the reader class name
   1.2 1.2 Communication protocol
   1.3 SLOT
   1.4 Card Status
   1.5 Get the IC/PSAM card control object
   1.6 Open the card reader
   1.7 Close the card reader
   1.8 Power on
   1.9 Power off
   1.10 Get the ATR string
   1.11 Get the communication protocol
   1.12 APDU Send
   1.13 Get the card status
   1.14 Detect card present
   1.15 SCM IC and Shenzhou IC switch
2 SLE4442Reader/SLE4428Reader
   2.1 Get the SLE4442Reader control object
   2.2 Get the SLE4448Reader control object
   2.3 Open the reader
   2.4 Close the reader
   2.5 Power on
   2.6 Power off
   2.7 Verify the password
   2.8 Change the password
   2.9 Read main memory
   2.10 Write main memory
1 Smart Card Reader
(com.common.apiutil.reader) IC/Psam


1.1 IC description of the reader class name

             Class Name                                  Description

              CardReader                         IC/PSAM Control base class

                                  The smart card control class conforms to ISO7816, inherited
           SmartCardReader
                                                  from the CardReader class

                                The SLE4442 card control class, inherited from the CardReader
            SLE4442Reader
                                                             class

                                The SLE4448 card control class, inherited from the CardReader
            SLE4428Reader
                                                             class


1.2 Communication protocol

                              Constant Name                                 Description

                     SmartCardReader.PROTOCOL_T0                              T0 protocol

                     SmartCardReader.PROTOCOL_T1                              T1 protocol

                     SmartCardReader.PROTOCOL_NA                          Other protocol


1.3 SLOT

                               Constant Name                                  Description

                           SmartCardReader.SLOT_ICC                                IC

                       SmartCardReader.SLOT_PSAM1                             PSAM Slot1

                       SmartCardReader.SLOT_PSAM2                             PSAM Slot2

                       SmartCardReader.SLOT_PSAM3                             PSAM Slot3

                       SmartCardReader.SLOT_PSAM4                             PSAM Slot4

                       SmartCardReader.SLOT_PSAM5                             PSAM Slot5

                       SmartCardReader.SLOT_PSAM6                             PSAM Slot6

                       SmartCardReader.SLOT_PSAM7                             PSAM Slot7

                       SmartCardReader.SLOT_PSAM8                             PSAM Slot8


1.4 Card status
                     Constant Name                            Description

                                                       The IC card is present and
          SmartCardReader.SLOT_STATUS_ICC_ACTIVE
                                                                activated

                                                      The IC card is present but not
         SmartCardReader.SLOT_STATUS_ICC_INACTIVE
                                                                activated

         SmartCardReader.SLOT_STATUS_ICC_ABSENT         The IC card is not present


1.5 Get the IC/PSAM card control object

  /**
   * @Function Obtain the IC/PSAM card control object
   *
   * @Parameter
   * 1.context
   * 2.slot
   *
   * */

  public SmartCardReader(Context context, int slot)



1.6 Open the card reader

  /**
   * @Function Open the card reader
   *
   * @Return true or false
   *
   * */

  public boolean open()



1.7 Close the card reader

  /**
   * @Function Close the card reader
   *
   * @Return true or false
   *
   * */

  public boolean close()



1.8 Power on
  /**
   * @Function Power on
   *
   * @Return true or false
   *
   * */

  public boolean powerOn()



1.9 Power off

  /**
   * @Function Power off
   *
   * @Return true or false
   *
   * */

  public boolean powerOff()



1.10 Get the ATR string

  /**
   * @Function Get the ATR string
   *
   *
   * @Return String
   *
   * */

  public String getATRString()



1.11 Get the communication protocol

  /**
   * @Function Get the communication protocol
   *
   * @Return int
   *
   * */

  public int getProtocol()



1.12 APDU Send
  /**
   * @Function Send the APDU
   *
   * @Parameter
   * 1.apdu: APDU command
   *
   * @Return Returned data; null: failed
   *
   * */

  public byte[] transmit(byte[] apdu) throws NullPointerException



1.13 Get the card status

  /**
   * @Function Get the card status
   *
   * @Return Card Type
   *
   * */

  public synchronized int getCardStatus()



1.14 Detect card present

  /**
   * @Function Detect card present
   *
   * @Return true or false
   *
   * */

  public boolean isCardPresent()



1.15 SCM IC and Shenzhou IC switch

  /**
   * Function SCM IC and Shenzhou IC switch
   *
   * @Parameter
   * 1.type: 0:SCM IC 1: Shenzhou IC
   *
   * @Return ResultCode: please refer the quick guidance document.
   *
   * */
   public synchronized int switchIcType(int type)




2 SLE4442Reader/SLE4428Reader
(com.common.apiutil.reader)

Support SLE4442 / SLE4428, inherit the CardReader class

2.1 Get the SLE4442Reader control object

   /**
    * @Function Get the SLE4442Reader control object
    *
    * @Parameter context
    *
    * */

   public SLE4442Reader(Context context)



2.2 Get the SLE4448Reader control object

   /**
    * @Function Get the SLE4448Reader control object
    *
    * @Parameter context
    *
    * */

   public SLE4428Reader(Context context)



2.3 Open the reader

   /**
    * @Function Open the reader
    *
    * @Return true or false
    *
    * */

   public boolean open()



2.4 Close the reader
    /**
     * @Function Close the reader
     *
     * @Return true or false
     *
     * */

    public boolean close()



2.5 Power on

    /**
     * @Function Power on
     *
     * @Return true or false
     *
     * */

    public boolean powerOn()



2.6 Power off

    /**
     * @Function Power off
     *
     * @Return true or false
     *
     * */

    public boolean powerOff()



2.7 Verify the password
Note: After successful veriﬁcation, it is valid until power down or reset



    /**
     * @Function Verify the password
     *
     * @Parameter
     * 1.psc
     *       1.SLE4442Reader: 3 bits
     *       2.SLE4428Reader: 2 bits
     *
     * @Return true or false
     *
     * @Exception InvalidParameterException, NullPointerException
     *
     * */

   public boolean pscVerify(byte[] psc) throws InvalidParameterException,
   NullPointerException



2.8 Change the password
Note: Before calling this API, verify password is called to change the password after the veriﬁcation is
successful



   /**
    * @Function Change the password
    *
    * @Parameter
    * 1.pscNew: new password
    *
    * @Return true or false
    *
    * @Exception InvalidParameterException, NullPointerException
    *
    * */

   public boolean pscModify(byte[] pscNew) throws InvalidParameterException,
   NullPointerException



2.9 Read main memory

   /**
    * @Function Read main memory
    *
    * @Parameter
    * 1.addr:
    *      Start address:
    *      1.SLE4442Reader: 0 ~ 255
    *      2.SLE4428Reader: 0 ~ 1023
    * 2.num:
    *      The number of bytes to read:
    *      1.SLE4442Reader: The range is up to 256 bytes
    *      2.SLE4428Reader: The range is up to 1024 bytes
    *
    * @Return Data; Null: Read failed
    *
    * */

   public byte[] readMainMemory(int addr, int num)
2.10 Write main memory
Note: Before calling this interface, you must call the validation password, and the data can only be written
after the veriﬁcation is successful



    /**
     * @Function Write main memory
     *
     * @Parameter
     * 1.addr:
     *      Start address:
     *      1.SLE4442Reader: 0 ~ 255
     *      2.SLE4428Reader: 0 ~ 1020
     *
     * 2.data:
     *      The number of bytes to read:
     *      1.SLE4442Reader: The range is up to 256 bytes
     *      2.SLE4428Reader: The range is up to 1021 bytes
     *
     * @Return true or false
     *
     * @Exception InvalidParameterException, NullPointerException
     *
     * */

    public boolean updateMainMemory(int addr, byte[] data) throws
    InvalidParameterException, NullPointerException

---

## input_output_control_api_en

         I/O Control API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2022-10-31              First Edition Release

2023-02-22                  Update API

2023-04-23             Update API and demo

2024-06-20         Update API and documentation
                                         Contents
1 Introduction
   1.1 LED
     1.1.1 LED Control
   1.2 Relay
     1.2.1 Relay Power On/Off
   1.3 GPIO Control
     1.3.1 Set the GPIO I/O
     1.3.2 Set the GPIO Level
     1.3.3 Get the GPIO Status
   1.4 Door/Magnetic Sensor/Physical Key/Wiegand
     1.4.1 Get the status of the sensor
     1.4.2 Wiegand Send
     1.4.3 Wiegand I/O Switch
     1.4.4 Input Signal Switch
     1.4.5 Register Wiegand/Key/Door input broadcast
     1.4.6 Unregister Wiegand/Key/Door input broadcast
     1.4.7 Set up Wiegand/Key/Door listening
     1.4.8 Receive the listening class
   1.5 RS232Reader/RS485Reader/RSTTLReader
     1.5.1 Get the RS232 Control Object
     1.5.2 Get the RS485 Control Object
     1.5.3 Get the RSTTL Control Object
     1.5.4 Open
     1.5.5 Destroy
     1.5.6 Send Data
     1.5.7 Set Up Receiving Listening
     1.5.8 RS232/RS485/RSTTL Irsreaderlistener
     1.5.9 Set the RS485 Transceiver Mode
     1.5.10 Get the Serial Port Number Path
1 Introduction
Package: (com.common.apiutil.pos)


Mainly for LED, Relay, Wiegand, RS232/485, Door Magnetic, Physical key to make a brief description


1.1 LED
1.1.1 LED Control


   /**
    * @Function Led Control
    *
    * @Parameter
    * 1.ledType: LED Type: please refer the quick guidance document.
    * 2.ledColor: LED Color: please refer the quick guidance document.
    * 3.brightness: brightness: [0 - 255], If it cannot be adjusted, and 0: Close;
   larger than 0: Open;
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static int setColorLed(int ledType, int ledColor, int brightness);



1.2 Relay
1.2.1 Relay power on/off


   /**
    * @Function Relay Control
    *
    * @Parameter
    * 1.type: Relay Type: please refer the quick guidance document.
    * 2.status: Close:0, Open:1
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static int setRelayPower(int type, int status)



1.3 GPIO Control
1.3.1 Set the GPIO I/O
   /**
    * @Function Set the GPIO Input/Output
    *
    * @Parameter
    * 1.type: GPIO Type: please refer the quick guidance document.
    * 2.direction: GPIO Direction: please refer the quick guidance document.
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static int setGPIOControl(int type, int direction)



1.3.2 Set the GPIO Level


   /**
    * @Function Set the GPIO Level
    *
    * @Parameter
    * 1.type: GPIO Type: please refer the quick guidance document.
    * 2.level: GPIO Level: please refer the quick guidance document.
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static int setGPIOLevel(int type, int level)



1.3.3 Get the GPIO Status


   /**
    * @Function Get the GPIO Status, Include input/output status and level status
    *
    * @Parameter
    * 1.type: GPIO Type: please refer the quick guidance document.
    *
    * @Return
    * GPIO Direction: please refer the quick guidance document.
    * GPIO Level: please refer the quick guidance document.
    * */

   public synchronized int[] getGPIOStatus(int type)



1.4 Door/Magnetic Sensor/Physical Key/Wiegand
1.4.1 Get the status of the sensor
   /**
    * @Function Gets the status of the sensor
    *
    * @Parameter
    * 1.type:
    *      0 -> Get the IR sensor status, 1: someone;0:none
    *      1 -> Get the removal sensor status, 1: dismantle;0:not dismantle
    *      2 -> Get the physical button door opening signal, 1: High level;0: Low
   level
    *      3 -> Acquire a magnetic susceptibility circuit signal, 1: High level;0:
   Low level
    *      4 -> Acquire a light sensor signal, 1:light;0: No light
    *      5 -> Obtain the bottom shell tamper signal, 1: dismantle;0: not dismantle
    *      6 -> Get the microwave status, 1:someone; 0:none
    *      7 -> Get the door status, 1:Open; 0:Close
    *      8 -> Get the door lock status, 1:Open;0:Close
    *      9 -> Obtain the magnetic induction circuit 1 state, 1:Open;0:Close
    *      10 -> Obtain the magnetic induction circuit 2 state , 1:Open;0:Close
    *
    * @Return 1; 0; -1, Failure
    *
    * */

   public synchronized void getPriximitySensorStatus(int type)



1.4.2 Wiegand Send


   /**
    * @Function Transmit Wiegand signals
    *
    * @Parameter
    * 1.type: Wiegand Type: please refer the quick guidance document.
    * 2.data: For hexadecimal data, the 26-bit Wiegand valid data bit is 24 bits, the
   34-bit Wiegand valid data bit is 32 bits, and the custom Wiegand is passthrough
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int wiegandSend(int type, byte[] data)



1.4.3 Wiegand I/O Switch


   /**
    * @Function Wiegand I/O Switch
    *
    * @Parameter
    * 1.direction: Signal Type: please refer the quick guidance document.
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int setWiegandDirection(int direction)



1.4.4 Input Signal Switch


   /**
    * @Function Toggle the input switch
    *
    * @Parameter
    * 1.input1: Input Type: please refer the quick guidance document.
    * 2.input2: Input Type: please refer the quick guidance document.
    * 3.sw: Input Status: please refer the quick guidance document.
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int switchInput(String input1, String input2, int sw)



1.4.5 Register Wiegand/Key/Door input broadcast


   /**
    * @Function Register Wiegand/Key/Door input broadcast
    *
    * @Parameter
    * 1.context
    *
    * @Return void
    *
    * */

   public synchronized void registerInputBroadcast(Context context)



1.4.6 Unregister Wiegand/Key/Door input broadcast


   /**
    * @Function Unregister Wiegand/Key/Door input broadcast
    *
    * @Return void
    *
    * */
   public synchronized void unRegisterInputBroadcast()



1.4.7 Set up Wiegand/Key/Door listening


   /**
    * @Function Set up Wiegan/Key/Door listening
    *
    * @Parameter
    * 1.inputListener: IInputListener
    *
    * @Return void
    *
    * */

   public synchronized void setInputListener(IInputListener inputListener)



1.4.8 Receive the listening class


   public interface IInputListener {
       /**
        * @Function Wiegand input
        *
        * @Parameter inputData
        *
        * */
       void wiegandInput(byte[] inputData);

       /**
        * @Function Magnetic door/Physical key/Magnetic Input
        *
        * @Parameter
        * 1.sw:
        *       1: Magnetic door
        *       2: Physical key
        *       3: Magnetic Input1
        *       4: Magnetic Input2
        * 2.status: 0:low level; 1:high level
        *
        * */
       void input(int sw, int status);
   }



1.5 RS232Reader/RS485Reader/RSTTLReader


(com.common.apiutil.pos) RS232/RS485/RSTTL
1.5.1 Get the RS232 control object


   /**
    * @Function Get the RS232 control object
    *
    * @Parameter context
    *
    * */

   public RS232Reader(Context context)



1.5.2 Get the RS485 control object


   /**
    * @Function Get the RS485 control object
    *
    * @Parameter context
    *
    * */

   public RS485Reader(Context context)



1.5.3 Get the RSTTL control object


   /**
    * @Function Get the RSTTL control object
    *
    * @Parameter context
    *
    * */

   public RSTTLReader(Context context)



1.5.4 Open


   /**
    * @Function Open RS232/RS485/RSTTL
    *
    * @Parameter
    * 1.type: RS232/RS485/RSTTL Type: please refer the quick guidance document.
    * 2.baud: baud rate
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int rsOpen(int type, int baud)



1.5.5 Destroy


   /**
    * @Function Destroy RS232/RS485/RSTTL
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int rsDestroy()



1.5.6 Send data


   /**
    * @Function Send the RS232/RS485/RSTTL data
    *
    * @Parameter data
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int rsSend(byte[] data)



1.5.7 Set up receiving listening


   /**
    * @Function Set up receiving monitoring
    *
    * @Parameter
    * 1.listenser: IRSReaderListener
    *
    * @Return void
    *
    * */

   public synchronized void setRSReaderListener(IRSReaderListener listener)



1.5.8 RS232/RS485/RSTTL IRSReaderListener
   /**
    * @Function RS232/RS485/RSTTL Receive the listening class
    *
    * */

   public synchronized interface IRSReaderListener{
       /**
        * @Parameter data
        *
        * */
       void onRecvData(byte[] data);
   }



1.5.9 Set the RS485 transceiver mode


   /**
    * @Function Set the RS485 transceiver mode
    *
    * @Parameter
    * 1.mode: RSMode: please refer the quick guidance document.
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public int setMode(int mode)



1.5.10 Get the serial port number path


   /**
    * @Function Get the serial port number path
    *
    * @Parameter
    * 1.type: RS232/RS485/RSTTL
    *
    * @Return serial port number path
    *
    * */

   public synchronized String getSerialPath(int type)

---

## magnetic_card_api_en

     Magnetic Card API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2022-10-31              First Edition Release

2023-02-22              Modify the interface

2024-06-20         Update API and documentation
                                     Contents
1 Introduction
   1.1 MagneticCard Class
     1.1.1 Open
     1.1.2 Close
     1.1.3 Start reading the magnetic stripe card
     1.1.4 Start reading the card
1 Introduction
1.1 MagneticCard Class
Package: com.common.apiutil.magnetic

1.1.1 Open


   /**
    * @Function Open the Magnetic Card and initialize
    *
    * @Exception CommonException
    *
    * */

   public static void open() throws CommonException



1.1.2 Close


   /**
    * @Function Close the Magnetic Card and release resources
    *
    * @Return void
    *
    * */

   public static void close()



1.1.3 Start reading the magnetic stripe card


   /**
    * @Function Start reading the magnetic stripe card, wait for the card to be
   swiped, and return after a successful swipe
    *
    * @Parameter
    * 1.timeout: Timeout, in ms
    *
    * @Return Returns an array of strings of length 3 corresponding to the
   information of the three tracks
    *
    * @Exception CommonException
    *
    * */

   public synchronized static String[] check(int timeout) throws CommonException
1.1.4 Start reading the card


   /**
    * @Function Start reading the card, check before calling
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized static int startReading()

---

## money_box_api_en

        Money Box API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2022-10-31              First Edition Release

2023-02-22           The API has been modified

2024-06-20         Update API and documentation
                                      Contents
1 Introduction
   1.1 MoneyBox Class
     1.1.1 Open the Money Box
     1.1.2 Open the Money Box(@Deprecated)
     1.1.3 Close the Money Box(@Deprecated)
     1.1.4 Get the Money Box Status
1 Introduction
1.1 MoneyBox Class
Package: com.common.apiutil.moneybox

1.1.1 Open the Money Box


   /**
    * @Function Open the Money Box, power on 450ms and power off automatically
    *
    * @Parameter
    * 1.which: start from 1
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static int open(int which)



1.1.2 Open the Money Box(@Deprecated)


   /**
    * @Function Open the Money Box, after power on it and power off within 500ms
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static synchronized int open()



1.1.3 Close the Money Box(@Deprecated)


   /**
    * @Function Close the Money Box
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public static synchronized int close()



1.1.4 Get the Money Box Status
/**
 * @Function Get the money box status
 *
 * @Parameter
 * 1.which: money box number: start with 1
 *
 * @Return ResultCode: please refer the quick guidance document.
 *
 * */

public static int getState(int which)

---

## nfc_api_en

                NFC API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2022-10-31              First Edition Release

2023-02-22              Modify the interface

2023-04-23             Update the interface

2024-06-20         Update API and documentation
                                         Contents
1 Introduction
   1.1 SelectCardReturn Class
     1.1.1 Card type enumeration
   1.2 NfcUtil Class
     1.2.1 Get Instance
     1.2.2 Initialize the serial port
     1.2.3 Destroy the serial port
     1.2.4 Check the microcontroller software version
     1.2.5 CPU Card-Select Card
     1.2.6 CPU Card-Send the APDU Command
     1.2.7 M1-Authentication
     1.2.8 M1-Read Block
     1.2.9 M1-Write Block
     1.2.10 M1-Close the Card
     1.2.11 M1-Initialize the e-wallet
     1.2.12 M1-Read the e-wallet
     1.2.13 M1-Write the e-wallet
   1.3 NFC Model
     1.3.1 Supported Card Types
     1.3.2 Mifare UltraLight EV1 card
     1.3.3 Mifare Desfire EV2/EV3 Card
     1.3.4 CPU Card
     1.3.5 Mifare Classic Card
     1.3.6 Felica Card
     1.3.7 Mifare Desfire EV1 Card
1 Introduction
Package: com.common.apiutil.nfc


1.1 SelectCardReturn Class

                           Class                           Description

                            Nfc                       Reflection interface

                           NfcUtil                            MCU

                      SelectCardReturn                 Card information


1.1.1 Card type enumeration


   /**
    * @Function Card type enumeration
    *
    * @Parameter
    * 1.cardType: M0,M1,ISO15693,FELICA,ID_CARD,TYPE_A,TYPE_B
    *
    * */

   public class SelectCardReturn {
       private CardTypeEnum cardType;
       private byte[] cardNum;
       public SelectCardReturn() {

        }

        public void setCardType(CardTypeEnum cardType) {
            this.cardType = cardType;
        }

        public CardTypeEnum getCardType() {
            return this.cardType;
        }
   }



1.2 NfcUtil Class
Supports identiﬁcation of CPU card and M1 card

1.2.1 Get Instance


   /**
    * @Function Get instance
    *
    * @Parameter context
    *
    * */

   public synchronized static final NfcUtil getInstance(Context context)



1.2.2 Initialize the serial port


   /**
    * @Function Initialize the serial port
    *
    * */

   public synchronized void initSerial()



1.2.3 Destroy the serial port


   /**
    * @Function Destroy the serial port
    *
    * */

   public synchronized void destroySerial()



1.2.4 Check the microcontroller software version


   /**
    * @Function Check the microcontroller software version
    *
    * @Return Microcontroller software version number
    *
    * */

   public synchronized int checkVersion()



1.2.5 CPU Card-Select Card


   /**
    * @Function CPU Card-Select Card
    *
    * */

   public synchronized SelectCardReturn selectCard()
1.2.6 CPU Card-Send the APDU Command


   /**
    * @Function CPU Card-Send the APDU Command
    *
    * */

   public synchronized byte[] sendAPDU(byte[] cmd, int timeout)



1.2.7 M1-Authentication


   /**
    * @Function M1-Authentication
    *
    * @Exception WrongParamException
    *
    * */

   public synchronized boolean M1_AUTHENTICATE(int block, int keyType, byte[] key)
   throws WrongParamException



1.2.8 M1-Read Block


   /**
    * @Function M1-Read Block
    *
    * */

   public synchronized byte[] M1_READ_BLOCK(int block) throws WrongParamException



1.2.9 M1-Write Block


   /**
    * @Function M1-Write Block
    *
    * @Exception WrongParamException
    *
    * */

   public synchronized boolean M1_WRITE_BLOCK(int block, byte[] data) throws
   WrongParamException
1.2.10 M1-Close the Card


   /**
    * @Function M1-Close the Card
    *
    * */

   public synchronized boolean M1_CLOSE_CARD()



1.2.11 M1-Initialize the e-wallet


   /**
    * @Function M1-Initialize the e-wallet
    *
    * @Parameter
    * 1.block: Write Block:0 - 255
    * 2.money: Value
    *
    * @Return True, success; false，failed
    *
    * @Exception WrongParamException
    *
    * */

   public boolean M1_INIT_WALLET(int block, int money) throws WrongParamException



1.2.12 M1-Read the e-wallet


   /**
    * @Function M1-Read the e-wallet
    *
    * @Parameter
    * 1.block: Write Block:0 - 255
    *
    * @Return Value
    *
    * @Exception WrongParamException,ReadWalletMoneyWrongException,
   NotInitWalletException
    *
    * */

   public Long M1_READ_WALLET(int block) throws WrongParamException,
   ReadWalletMoneyWrongException, NotInitWalletException



1.2.13 M1-Write the e-wallet
   /**
    * @Function M1-Write the e-wallet
    *
    * @Parameter
    * 1.block: Write Block:0 - 255
    * 2.money: Value
    * 3.dealType: Type:0, Add；1, Sub
    *
    * @Return True, success; false, failed
    *
    * @Exception WrongParamException
    *
    * */

   public boolean M1_WRITE_WALLET(int block, int dealType, int money) throws
   WrongParamException




                             Type                                   Description

                     WrongParamException                     Parameter error exception

                ReadWalletMoneyWrongException              Read e-wallet error exception

                    NotInitWalletException               E-wallet is not initialized exception

                   NoClassObjectsException               The required class was not found


1.3 NFC Model
1.3.1 Supported Card Types

It depends on the speciﬁc NFC chip and terminal device

1.3.2 Mifare UltraLight EV1 card

a Write Block



   /**
    * @Function Write Block
    *
    * @Parameter
    * 1.noBlock: Block
    * 2.inBuf: Data
    * 3.inLen: Length
    *
    * @Exception CommonException
    *
    * */
   public synchronized void ultraigh_write_page(byte noBlock, byte[] inBuf, int
   inLen) throws CommonException



b Read Block



   /**
    * @Function Read Block
    *
    * @Parameter
    * 1.noBlock: Block
    *
    * @Exception CommonException
    *
    * */

   public synchronized byte[] ultraigh_read_page(byte noBlock) throws CommonException



c Ultraigh EV1 Password Veriﬁcation



   /**
    * @Function Ultraigh EV1 Password verification
    *
    * @Parameter
    * 1.noBlock: 0-255
    * 2.pwd: A 4-byte password
    *
    * @Return success：2 bytes. Failed：exception
    *
    * @Exception CommonException
    *
    * */

   public synchronized byte[] ultraigh_pwd_auth(byte noBlock, byte[] pwd) throws
   CommonException



1.3.3 Mifare Desﬁre EV2/EV3 Card

./NFC/Deﬁre_Ev2&Ev3_Card.pdf

1.3.4 CPU Card

./NFC/cpu_card_api.pdf

1.3.5 Mifare Classic Card

./NFC/mifare_classic_card_api.pdf

1.3.6 Felica Card
./NFC/felica_card_api.pdf

1.3.7 Mifare Desﬁre EV1 Card

./NFC/desﬁre_ev1_card_api.pdf

---

## power_control_api_en

     Power Control API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2023-04-23              First Edition Release

2023-12-19                Update the API

2024-06-20         Update API and documentation
                                        Contents
1 Introduction
   1.1 Power Control Class
     1.1.1 Fingerprint device power on/off
     1.1.2 Iris power on/off
     1.1.3 Passport power on/off
     1.1.4 Ethernet power on/off
     1.1.5 USB camera power on/off
     1.1.6 ID Card power on/off
     1.1.7 IC Card power on/off
     1.1.8 Psam Card power on/off
     1.1.9 UHF power on/off
     1.1.10 QR Code power on/off
     1.1.11 Zink Printer power on/off
     1.1.12 Self-developed printer power on/off
     1.1.13 USB/OTG Switch
     1.1.14 USB/NFC power on/off
     1.1.15 Other device power on/off
     1.1.16 Buzzer power on/off
     1.1.17 USB power on/off
     1.1.18 Lan power on/off
     1.1.19 HDMI power on/off
     1.1.20 MPos power on/off
     1.1.21 Crypto card power on/off
     1.1.22 Get the DIP switch status
     1.1.23 Get the case box switch status
1 Introduction
Package: (com.common.apiutil.powercontrol)
Support control of ﬁngerprint reader, 2D reading head, iris, passport, Ethernet and USB


1.1 Power Control Class
1.1.1 Fingerprint device power on/off


   /**
    * @Function Fingerprint device power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int fingerPrintPower(int status)



1.1.2 Iris power on/off


   /**
    * @Function Iris power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int irisPower(int status)



1.1.3 Passport power on/off


   /**
    * @Function Passport power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int pspPower(int status)



1.1.4 Ethernet power on/off


   /**
    * @Function Ethernet power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int ethernetPower(int status)



1.1.5 USB camera power on/off


   /**
    * @Function USB camera power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int usbCameraPower(int status)



1.1.6 ID Card power on/off


   /**
    * @Function ID Card power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int idcardPower(int status)
1.1.7 IC Card power on/off


   /**
    * @Function IC Card power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int iccardPower(int status)



1.1.8 Psam Card power on/off


   /**
    * @Function Psam Card power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int psamcard(int status)



1.1.9 UHF power on/off


   /**
    * @Function UHF power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int uhfPower(int status)



1.1.10 QR Code power on/off
   /**
    * @Function QR Code power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int decodePower(int status)



1.1.11 Zink Printer power on/off


   /**
    * @Function Zink Printer power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int zinkPower(int status)



1.1.12 Self-developed printer power on/off


   /**
    * @Function Self-developed printer power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int printerPower(int status)



1.1.13 USB/OTG Switch


   /**
    * @Function USB/OTG switch
    *
    * @Parameter
    * 1.status: 0, ADB; 1, OTG
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int usbPower(int status)



1.1.14 USB/NFC power on/off


   /**
    * @Function NFC power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int nfcPower(int status)



1.1.15 Other device power on/off


   /**
    * @Function Other device power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    * 2.device: Device identification character
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int miscPower(String device, int status)



1.1.16 Buzzer power on/off


   /**
    * @Function Buzzer power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int beepPower(int status)



1.1.17 USB power on/off


   /**
    * @Function USB power on/off
    *
    * @Parameter
    * 1.which: Start with 1
    * 2.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public int usbPower(int which, int status)



1.1.18 Lan power on/off


   /**
    * @Function Lan power on/off
    *
    * @Parameter
    * 1.which: Start with 1
    * 2.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public int lanPower(int which, int status)



1.1.19 HDMI power on/off


   /**
    * @Function HDMI power on/off
    *
    * @Parameter
    * 1.which: Start with 1
    * 2.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public int hdmiPower(int which, int status)



1.1.20 MPos power on/off


   /**
    * @Function MPos power on/off
    *
    * @Parameter
    * 1.which: Start with 1
    * 2.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public int mposPower(int which, int status)



1.1.21 Crypto card power on/off


   /**
    * @Function Crypto card power on/off
    *
    * @Parameter
    * 1.status: 0, Power off; 1, Power on
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public int cryptoCardPower(int status)



1.1.22 Get the DIP switch status


   /**
    * @Function Get the DIP switch status
    *
    * @Return 0, close; 1, open; ResultCode: please refer the quick guidance
   document.
    *
    * */

   public int getDialSwitchStatus()
1.1.23 Get the case box switch status


   /**
    * @Function Get the case box switch status
    *
    * @Return 0, close; 1, open; ResultCode: please refer the quick guidance
   document.
    *
    * */

   public int getCaseSwitchStatus()

---

## printer_api_en

             Printer API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2022-10-31              First Edition Release

2023-02-22              Modify the interface

2023-04-23             Update the interface

2024-06-20         Update API and documentation
                                        Contents
1 Introduction
   1.1 Detect the printer type
   1.2 UsbThermalPrinter Class
     1.2.1 UsbThermalPrinter
     1.2.2 Get the Model Version
     1.2.3 Get the printer status
     1.2.4 Printer reset
     1.2.5 Look for black labels
     1.2.6 Paper Feed
     1.2.7 Set the print alignment
     1.2.8 Set the Left Margin
     1.2.9 Set the Line Spacing
     1.2.10 Font Bold
     1.2.11 Set the Font Size
     1.2.12 Set the Print Grayscale Value
     1.2.13 Add Print Content
     1.2.14 Start Printing
     1.2.15 Get the Total Width of the Input Text
     1.2.16 Adds an inline offset print
     1.2.17 Add contents of the current printed line at the end
     1.2.18 Print picture/Bar code/QR code
     1.2.19 Table printing, printString is not executed
     1.2.20 Set italics
     1.2.21 Set the print paper type
     1.2.22 Adapt the new label paper
     1.2.23 Feed out the label paper
     1.2.24 Roll back the label paper
   1.3 ThermalPrinter Class
     1.3.1 Printer width
     1.3.2 Printer language type
     1.3.3 Printer status
     1.3.4 Initialize the 80mm USB printer
     1.3.5 Initialize the 80mm serial printer
     1.3.6 Printer Resource Request
     1.3.7 Get the Model Version
     1.3.8 Printer Reset
     1.3.9 Look for Black Labels
     1.3.10 Paper Feed
     1.3.11 Set the Print Alignment
     1.3.12 Set the Font Size
     1.3.13 The Print Font Width and Height are Enlarged
     1.3.14 Set the Print Grayscale Value
1.3.15 Add Print Content
1.3.16 Start Printing
1.3.17 Paper Cut
1.3.18 Send Instructions to the Printer Device
1.3.19 Print the Picture
1.3.20 Release the Printer
1.3.21 Set the Anti-white
1.3.22 Set the paper width
1.3.23 Get the print count
1.3.24 Get printer status
1 Introduction
1.1 Detect the printer type

   int printerCheck = SystemUtil.checkPrinter581(this);
   if(printerCheck == PRINTER_80MM_USB_COMMON){
       //SY581 USB printer
   }else if(printerCheck == PRINTER_SY581){
       //SY581 Serial printer
   }else if(printerCheck == PRINTER_SY581){
       //58mm Serial/USB printer
   }



1.2 UsbThermalPrinter Class
Package: com.common.apiutil.printer
Thermal USB/Serial printer interface(56 mm)

1.2.1 UsbThermalPrinter


   /**
    * @Function UsbThermalPrinter
    *
    * @Parameter context
    *
    * */

   public UsbThermalPrinter(Context context)



1.2.2 Get the Model Version


   /**
    * @Function Get the model version
    *
    * @Return Model version
    *
    * @Exception CommonException
    *
    * */

   public synchronized String getVersion() throws CommonException



1.2.3 Get the printer status
   /**
    * @Function Get the printer status
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * @Exception CommonException
    *
    * */

   public synchronized static int checkStatus() throws CommonException



1.2.4 Printer reset


   /**
    * @Function The printer resets, all settings return to their default settings,
   and the print buffer is cleared
    *
    * @Exception CommonException
    *
    * */

   public synchronized void reset() throws CommonException



1.2.5 Look for black labels


   /**
    * @Function Look for black labels
    *
    * @Parameter
    * 1.search_disdance: Find the maximum number of paper lines before the black
   label, 0-255
    * 2.walk_disdance: The number of paper lines after the black label is found, 0-
   255
    *
    * @Exception CommonException
    *
    * */

   public synchronized void searchMark(int search_disdance, int walk_disdance) throws
   CommonException



1.2.6 Paper Feed


   /**
    * @Function Paper Feed
    *
    * @Parameter
    * 1.line: int > 0
    *
    * @Exception CommonException
    *
    * */

   public synchronized void walkPaper(int line) throws CommonException



1.2.7 Set the print alignment


   /**
    * @Function Set the print alignment
    *
    * @Parameter
    * 1.mode:
    *      1.ALGIN_LEFT
    *      2.ALGIN_MIDDLE
    *      3.ALGIN_RIGHT
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setAlgin(int mode) throws CommonException



1.2.8 Set the Left Margin


   /**
    * @Function Set the left margin
    *
    * @Parameter
    * 1.leftDistance: Margin value: 0-255, Unit: 0.125mm
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setLeftIndent(int leftDistance) throws CommonException



1.2.9 Set the Line Spacing


   /**
    * @Function Set the line spacing
    *
    * @Parameter
    * 1.lineDistance: 0-255
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setLineSpace(int lineDistance) throws CommonException



1.2.10 Font Bold


   /**
    * @Function Font bold
    *
    * @Parameter
    * 1.isBold: True: font bold; False: font not bold
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setBold(boolean isBold) throws CommonException



1.2.11 Set the Font Size


   /**
    * @Function Set the font size
    *
    * @Parameter
    * 1.size: 8-64
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setTextSize(int size) throws CommonException



1.2.12 Set the Print Grayscale Value


   /**
    * @Function Set the print grayscale value
    *
    * @Parameter
    * 1.gray: Need to check if the hardware supports it
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setGray(int gray) throws CommonException



1.2.13 Add Print Content


   /**
    * @Function Add print content
    *
    * @Parameter
    * 1.content: print content
    *
    * @Exception CommonException
    *
    * */

   public synchronized void addString(String content) throws CommonException



1.2.14 Start Printing


   /**
    * @Function Start printing, and the paper will not be taken after printing
    *
    * @Exception CommonException
    *
    * */

   public synchronized void printString() throws CommonException



1.2.15 Get the Total Width of the Input Text


   /**
    * @Function Gets the total width of the input tex
    *
    * @Parameter
    * 1.text: text content
    *
    * @Exception CommonException
    *
    * */

   public synchronized int measureText(String text) throws CommonException



1.2.16 Adds an inline offset print
    /**
     * @Function Adds an inline offset print
     *
     * @Parameter
     * 1.offset: Inline offset, which must be an integer multiple of 8, cannot be less
    than the left margin, and cannot be greater than (384-right margin)
     * 2.content: String content
     *
     * @Exception CommonException
     *
     * */

    public synchronized void addStringOffset(int offset, String content) throws
    CommonException



Tips: This function is suitable for USB printing two stages, USB printing one stage is not valid

           1. Note that this method can only be added with content within one line, and characters beyond
             one line will be intercepted and not printed
           2. When adding content within the same line, offset needs to be added from small to large
           3. When content is added within the same line, the print format is not allowed in the middle,
             otherwise it will cause word wrapping
           4. During the same line addition process, calling the "addString()" method automatically wraps
           5. Add the contents of this line at the end of the line and call the "endLine()" method

1.2.17 Add contents of the current printed line at the end


    /**
     * @Function Adds the contents of the current printed line to wrap after the
    addStringOffset method call
     *
     * @Exception CommonException
     *
     * */

    public synchronized void endLine() throws CommonException



1.2.18 Print picture/Bar code/QR code


    /**
     * @Function Print picture/Bar code/QR code
     *
     * @Parameter
     * 1.image: Bitmap objects, the width must be less than 384 pixels and an integer
    multiple of 8, and the high pixels must be an integer of 8
     * 2.isBuffer: Whether to join the buffer queue, call printString or
    printStringAndWalk, the buffer queue is used. There is data printed together,
   mostly used for mixed printing of graphics and text
    *
    * @Exception CommonException
    *
    * */

   public synchronized void printLogo(Bitmap image, boolean isBuffer) throws
   CommonException

   //Bar code example
   Bitmap bitmap = CreateCode(barcodeStr, BarcodeFormat.CODE_128, 320, 176);
   if (bitmap != null) {
       mUsbThermalPrinter.printLogo(bitmap, true);
   }



1.2.19 Table printing, printString is not executed


   /**
    * @Function Table printing, printString is not executed
    *
    * @Parameter
    * 1.colsTestArr: An array of column text strings
    * 2.colsWidthArr: The width weight of each column is the proportion of each
   column
    * 3.colsAlign: Column alignment: 0 Left, 1 center, 2 right
    * 4.colsTextSize: Font size 12/24/36
    *
    * @Exception CommonException
    *
    * */

   public synchronized void addColumnsString(String[] colsTestArr, int[]
   colsWidthArr, int[] colsAlign, int colsTextSize) throws CommonException



1.2.20 Set italics


   /**
    * @Function Set italics
    *
    * @Parameter isItalic
    *
    * @Exception CommonException
    *
    * */

   public void setItalic(boolean isItalic) throws CommonException



1.2.21 Set the print paper type
   /**
    * @Function Set the print paper type
    *
    * @Parameter
    * 1.mode: 0, normal paper; 2, label paper; default: label paper
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setAlgorithm(int mode) throws CommonException



1.2.22 Adapt the new label paper


   /**
    * @Function Adapt the new label paper
    *
    * @Parameter
    * 1.line: line number(mm)
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setLableAdapt(int line) throws CommonException



1.2.23 Feed out the label paper


   /**
    * @Function Feed out the label paper
    *
    * @Parameter
    * 1.line: line number(mm)
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setLableFeedOutNextGap(int line) throws CommonException



1.2.24 Roll back the label paper


   /**
    * @Function Roll back the label paper
    *
    * @Parameter
    * 1.line: line number(mm)
    *
    * @Exception CommonException
    *
    * */

   public synchronized void rollback(int line) throws CommonException



1.3 ThermalPrinter Class
Package: com.common.apiutil.printer
Thermal USB/Serial printer interface(80mm)

1.3.1 Printer width

                                      Type                    Description

                        ThermalPrinter.PAPER_80mm                80mm

                        ThermalPrinter.PAPER_58mm                58mm


1.3.2 Printer language type

                                   Type                       Description

                          ThermalPrinter.ENGLISH                English

                          ThermalPrinter.PERSIAN                Persian

                          ThermalPrinter.KOREAN                 Korean

                         ThermalPrinter.JAPANESE               Japanese

                          ThermalPrinter.FRENCH                 French

                          ThermalPrinter.RUSSIAN                Russian

                         ThermalPrinter.THAILAND               Thailand

                         ThermalPrinter.CAMBODIA               Cambodia

                           ThermalPrinter.LAOS                   Laos

                          ThermalPrinter.CHINESE                Chinese

                        ThermalPrinter.PORTUGUESE             Portuguese

                          ThermalPrinter.SPANISH                Spanish

                          ThermalPrinter.ITALIAN                Italian

                          ThermalPrinter.GERMAN                 German

                    ThermalPrinter.HONGKONG_CHINA           China HongKong
1.3.3 Printer status

                            Type                             Description

                   ThermalPrinter.STATUS_OK              The status is normal

                ThermalPrinter.STATUS_NO_PAPER                 No paper

               ThermalPrinter.STATUS_OVER_HEAT                 Over heat

               ThermalPrinter.STATUS_OVER_FLOW              The cache is full

               ThermalPrinter.STATUS_UNKNOWN                Unknown error

               ThermalPrinter.STATUS_BOX_OPEN         The printer box is opened

              ThermalPrinter.STATUS_CUT_WRONG        The printer cutter is incorrect


1.3.4 Initialize the 80mm USB printer


   /**
    * @Function Initialize the 80mm USB printer
    *
    * */

   public synchronized static boolean init80mmUsbPrinter(Context context)



1.3.5 Initialize the 80mm serial printer


   /**
    * @Function Initialize the 80mm serial printer
    *
    * */

   public synchronized static boolean init80mmSerialPrinter()



1.3.6 Printer Resource Request


   /**
    * @Function Printer resource request
    *
    * @Tips Printer resource application, you must first apply to use the printer-
   related operations, after using the stop() method to release
    *
    * @Exception CommonException
    *
    * */

   public synchronized static void start(Context context) throws CommonException
1.3.7 Get the Model Version


   /**
    * @Function Get model version
    *
    * @Return Model version
    *
    * @Exception CommonException
    *
    * */

   public synchronized static String getVersion() throws CommonException



1.3.8 Printer Reset


   /**
    * @Function Printer reset
    *
    * @Tips The printer resets, all settings return to their default settings, and
   the print buffer is cleared
    *
    * @Exception CommonException
    *
    * */

   public synchronized static void reset() throws CommonException



1.3.9 Look for Black Labels


   /**
    * @Function Look for black labels
    *
    * @Parameter
    * 1.search_disdance: 0-255
    * 2.walk_disdance: 0-255
    *
    * @Exception CommonException
    *
    * */

   public synchronized void searchMark(int search_disdance, int walk_disdance) throws
   CommonException



1.3.10 Paper Feed
   /**
    * @Function Paper feed
    *
    * @Parameter
    * 1.line: line number [int > 0]
    *
    * @Exception CommonException
    *
    * */

   public synchronized void walkPaper(int line) throws CommonException



1.3.11 Set the Print Alignment


   /**
    * @Function Set the print alignment
    *
    * @Parameter
    * 1.mode:
    *      1.ALGIN_LEFT
    *      2.ALGIN_MIDDLE
    *      3.ALGIN_RIGHT
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setAlgin(int mode) throws CommonException



1.3.12 Set the Font Size


   /**
    * @Function Set the font size
    *
    * @Parameter
    * 1.type:
    *          - Font size setting, 1-2 defaults to the No.2 font size
    *          - For single-byte characters, the font size of No.1 font size is 8*16
   and the font size of No.2 font size is 12*24
    *          - For double-byte characters, the font size of No.1 font size is 16*16
   and the font size of No.2 font size is 24*24
    *
    * @Exception CommonException
    *
    * */

   public synchronized static void setFontSize(int type) throws CommonException
1.3.13 The Print Font Width and Height are Enlarged


   /**
    * @Function The print font width and height are enlarged
    *
    * @Parameter
    * 1.widthMultiple: Default, 1
    * 2.heightMultiple: Default, 1
    *
    * @Exception CommonException
    *
    * */

   public synchronized static void enlargeFontSize(int widthMultiple, int
   heightMultiple) throws CommonException



1.3.14 Set the Print Grayscale Value


   /**
    * @Function Set the print grayscale value
    *
    * @Parameter
    * 1.level: Need to check if your hardware supports it
    *
    * @Exception CommonException
    *
    * */

   public synchronized void setGray(int level) throws CommonException



1.3.15 Add Print Content


   /**
    * @Function Add print content
    *
    * @Parameter
    * 1.content: String content
    *
    * @Exception CommonException
    *
    * */

   public synchronized void addString(String content) throws CommonException



1.3.16 Start Printing
   /**
    * @Function Start printing, and the paper will not be taken after printing
    *
    * @Parameter
    * 1.enctype: Such as ENGLISH, PERSIAN, KOREAN, JAPANESE, CHINESE
    *
    * @Exception CommonException
    *
    * */

   public synchronized static void printString(String enctype) throws CommonException



1.3.17 Paper Cut


   /**
    * @Function Paper cut
    *
    * @Exception CommonException
    *
    * */

   public synchronized static void paperCut() throws CommonException



1.3.18 Send Instructions to the Printer Device


   /**
    * @Function Send instructions to the printer device
    *
    * @Parameter
    * 1.cmdStr: byte[] Command
    * 2.len: length
    *
    * @Exception CommonException
    *
    * */

   public synchronized static void sendCommand(byte[] cmdStr, int len) throws
   CommonException



1.3.19 Print the Picture


   /**
    * @Function Print the picture
    *
    * @Parameter
    * 1.image: Image resource Bitmap object, width must be less than 384 pixels and
   an integer multiple of 8, high pixels must be an integer multiple of 8
    *
    * */

   public synchronized void printLogo(Bitmap image, boolean isBuffer) throws
   CommonException

   //Barcode example
   Bitmap bitmap = CreateCode(barcodeStr, BarcodeFormat.CODE_128, 320, 176);
   if (bitmap != null) {
       mUsbThermalPrinter.printLogo(bitmap, true);
   }
   mUsbThermalPrinter.addString(barcodeStr);
   mUsbThermalPrinter.printString();



1.3.20 Release the Printer


   /**
    * @Function Release the printer, which must be released when the printer is not
   in use
    *
    * */

   public synchronized static void stop(Context context)



1.3.21 Set the Anti-white


   /**
    * @Function Set the anti-white
    *
    * @Parameter
    * 1.isInverse: True or false
    *
    * @Exception CommonException
    *
    * */

   public static void setInverse(boolean isInverse) throw CommonException



1.3.22 Set the paper width


   /**
    * @Function Set the paper width
    *
    * @Parameter
    * 1.paperWidth: Paper width
    *
    * */

   public static void setPaperWidth(int paperWidth)



1.3.23 Get the print count


   /**
    * @Function Get the print count
    *
    * @Return Printed count
    *
    * */

   public static long getPrintCount()



1.3.24 Get printer status


   /**
    * @Function Get printer status
    *
    * @Exception CommonException
    *
    * */

   public synchronized static int checkStatus() throws CommonException

---

## qrcode_api_en

           QR Code API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2022-10-31              First Edition Release

2023-02-22                  Update API

2023-04-23             Update API and demo

2024-06-20         Update API and documentation
                                    Contents
1 Introduction
   1.1 Get the dock sweep control object
   1.2 Open
   1.3 Close
   1.4 Set up receiving listening
   1.5 Scan dock to receive monitoring class
   1.6 Send Command
1 Introduction
(com.common.apiutil.decode)

It includes an interface for opening and closing hard read heads and obtaining scan information.


1.1 Get the dock sweep control object

   /**
    * @Function Get the dock sweep control object
    *
    * @Parameter context
    *
    * */

   public DecodeReader(Context context)



1.2 Open

   /**
    * @Function Turn on and power on the scan code read head
    *
    *
    * @Parameter
    * 1.baud: Baudrate
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public int open(int baud)



1.3 Close

   /**
    * @Function Turn off and power down the scan code read head
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public int close()



1.4 Set up receiving listening
  /**
   * @Function Set up data monitoring for terminal sweeping
   *
   * @Parameter listener: IDecodeReaderListener
   *
   * @Return void
   *
   * */

  public void setDecodeReaderListener(IDecodeReaderListener listener)



1.5 Scan dock to receive monitoring class

  /**
   * @Function Scan dock to receive monitoring class
   *
   * */

  public interface IDecodeReaderListener{
      /**
       * @Parameter data
       *
       * */
      void onRecvData(byte[] data);
  }



1.6 Send Command

  /**
   * @Function Send command
   *
   * @Parameter data
   *
   * @Return ResultCode: please refer the quick guidance document.
   *
   * */

  public synchronized int cmdSend(byte[] data)

---

## simple_lcd_api_en

        Simple LCD API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2023-04-23              First Edition Release

2024-06-20         Update API and documentation
                                     Contents
1 Introduction
   1.1 SimpleSubLcd Class
     1.1.1 Get the smile screen control object
     1.1.2 Init
     1.1.3 Release
     1.1.4 Show
     1.1.5 Get the version
     1.1.6 Update the MCU firmware
1 Introduction
1.1 SimpleSubLcd Class
1.1.1 Get the smile screen control object


   /**
    * @Function Get the smile screen control object
    *
    * @Parameter context
    *
    * */

   public SimpleSubLcd(Context context)



1.1.2 Init


   /**
    * @Function Init LCD
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int init()



1.1.3 Release


   /**
    * @Function Release LCD
    *
    * */

   public synchronized void release()



1.1.4 Show


   /**
    * @Function Show the picture
    *
    * @Parameter
    * 1.picFile: Image path, the format needs to be supported by the module
    *
    * @Return: ResultCode: please refer the quick guidance document.
    *
    * */

   public synchronized int show(String picFile)



1.1.5 Get the version


   /**
    * @Function Get the version
    *
    * @Return Version number
    *
    * */

   public synchronized String getVersion()



1.1.6 Update the MCU ﬁrmware


   /**
    * @Function Update the MCU firmware
    *
    * @Parameter
    * 1.binFile: File path
    *
    * @Return ResultCode: please refer the quick guidance document.
    *
    * */

   Public synchronized int update(String binFile)

---

## system_api_en

            System API
Development Documentation
                     V1.1.0
             Update Records
  Date                      Description

2022-10-31              First Edition Release

2023-02-22                Modify the API

2024-06-20         Update API and documentation
                                     Contents
1 Introduction
   1.1 SystemApiUtil Class
     1.1.1 SystemApiUtil
     1.1.2 Show the Status Bar
     1.1.3 Hide the Status Bar
     1.1.4 Show the Navigation Bar
     1.1.5 Hide the Navigation Bar
     1.1.6 Set the System Time
     1.1.7 Silent Installation
     1.1.8 Register for Broadcasting1
     1.1.9 Unbind Broadcast
     1.1.10 Reboot Device
     1.1.11 Shut Down the Device
   1.2 SystemUtil Class
     1.2.1 Get CPU usage
     1.2.2 Get memory usage
     1.2.3 Get CPU temperature
     1.2.4 Determine the module of NFC(PN512)
     1.2.5 Obtain th SDK functions supported by the device
1 Introduction
1.1 SystemApiUtil Class
Package: com.common.apiutil.system
Includes show/hide status bar, show/hide navigation bar, set system time, silent installation, register/unbind
broadcast, restart, shutdown

1.1.1 SystemApiUtil


   /**
    * @Function Get the SystemApiUtil control object
    *
    * @Parameter context
    *
    * */

   public SystemApiUtil (Context context)



1.1.2 Show the Status Bar


   /**
    * @Function Show the status bar
    *
    * */

   public synchronized void showStatusBar()



1.1.3 Hide the Status Bar


   /**
    * @Function Hide the status bar
    *
    * */

   public synchronized void hideStatusBar()



1.1.4 Show the Navigation Bar


   /**
    * @Function Show the navigation bar
    *
    * */
   public synchronized void showNavigationBar()



1.1.5 Hide the Navigation Bar


   /**
    * @Function Hide the navigation bar
    *
    * */

   public synchronized void hideNavigationBar()



1.1.6 Set the System Time


   /**
    * @Function Set the system time
    *
    * @Parameter
    * 1.timeInMillis: Time
    *
    * */

   public synchronized void setSystemTime(long timeInMillis)



1.1.7 Silent Installation


   /**
    * @Function Silent installation
    *
    * @Parameter
    * 1.appPath: Path name
    * 2.packageName: Package name
    *
    * */

   public synchronized void installApp(String appPath, String packageName)



1.1.8 Register for Broadcasting


   /**
    * @Function Register for broadcasting
    *
    * Register for broadcast, silently install immediately wake up the app (call
   before invoking installApp)
    *
    * */

   public synchronized void registerWakeUpAppBroadcast()



1.1.9 Unbind Broadcast


   /**
    * @Function Unbind broadcast
    *
    * */

   public synchronized void unRegisterWakeUpAppBroadcast()



1.1.10 Reboot Device


   /**
    * @Function Reboot Device
    *
    * */

   public synchronized void rebootDevice()



1.1.11 Shut Down the Device


   /**
    * @Function Shut down
    *
    * */

   public synchronized void shutdown()



1.2 SystemUtil Class
Package: com.common.apiutil.util
System property control API

1.2.1 Get CPU usage


   /**
    * @Function Get the CPU usage
    *
    * @Return CPU usage; ResultCode: please refer the quick guidance document.
    *
    * */

   public static int getCpuRate()



1.2.2 Get memory usage


   /**
    * @Function Get memory usage
    *
    * @Return Memory usage; ResultCode: please refer the quick guidance document.
    *
    * */

   public static int getMemRate()



1.2.3 Get CPU temperature


   /**
    * @Function Get CPU temperature
    *
    * @Return CPU temperature; ResultCode: please refer the quick guidance document.
    *
    * */

   public static int getCpuTem()



1.2.4 Determine the module of NFC(PN512)


   /**
    * @Function Determine the module of NFC
    *
    * @Return True or false
    *
    * */

   public static boolean isPN512NFC()



1.2.5 Obtain the functions supported


   /**
    * @Function Obtain the functions supported
    *
    * @Return
    * Print = 1
 * QR Code = 2
 * Magnetic Stripe Card = 3
 * Money box = 4
 * ID Card = 5
 * IC Card = 6
 * NFC = 7
 * PSAM = 8
 * Tamper Test = 9
 * LED = 10
 * Relay = 11
 * RS485/232 = 12
 * Wiegand/Door Magnetic = 13
 * Nixie tube test = 14
 * System API = 15
 * Can Bus = 16
 * GPIO = 17
 * Serial port= 18
 * Sample LCD = 19
 *
 * */

public static String[] getDeviceSupport()

---

