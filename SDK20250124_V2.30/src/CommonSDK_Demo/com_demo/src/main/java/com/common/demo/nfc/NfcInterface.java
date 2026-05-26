package com.common.demo.nfc;

public interface NfcInterface {

    int OP_NFC_Device_Open();

    int OP_NFC_Device_Close();

    int OP_NFC_Device_Activate(byte[] Parm, byte[] outData, int[] outLen);

    int OP_CPU_GET_ATS(byte[] outData, int[] outLen);

    int OP_activateEv2ForNewCard();

    int OP_getFreeMem(byte[] outData, int[] outLen);

    int OP_Format();

    int OP_GetVersion(byte[] outData, int[] outLen);

    int OP_GetCardUID(byte[] outData, int[] outLen);

    int OP_GetKeySettings(byte[] AID, byte[] outData, int[] outLen);

    int OP_GetKeyVersion(byte[] Parm, byte[] outData, int[] outLen) ;

    int OP_CreateApplication(byte[] Parm);

    int OP_DeleteApplication(byte[] AID);

    int OP_CreateDelegatedApplication(byte[] Parm);

    int OP_SelectApplication(byte[] AID);

    int OP_GetApplicationIDs(byte[] outData, int[] outLen);

    int OP_GetDelegatedInfo(byte[] DAMSlotNo, byte[] outData, int[] outLen);

    int OP_CreateStdDataFile(byte[] Parm);

    int OP_CreateBackupDataFile(byte[] Parm);

    int OP_CreateValueFile(byte[] Parm);

    int OP_CreateLinearRecordFile(byte[] Parm);

    int OP_CreateCyclicRecordFile(byte[] Parm);

    int OP_CreateTransactionMACFile(byte[] Parm);

    int OP_DeleteFile(byte[] Parm);

    int OP_GetFileIDs(byte[] AID, byte[] outData, int[] outLen);

    int OP_GetFileSettings(byte[] Parm, byte[] outData, int[] outLen);

    int OP_GetFileCounters(byte[] Parm, byte[] outData, int[] outLen);

    int OP_WriteData(byte[] Parm);

    int OP_ReadData(byte[] Parm, byte[] outData, int[] outLen);

    int OP_Credit(byte[] Parm);

    int OP_LimitedCredit(byte[] Parm);

    int OP_Debit(byte[] Parm);

    int OP_GetValue(byte[] Parm, byte[] outData, int[] outLen);

    int OP_WriteRecord(byte[] Parm);

    int OP_UpdateRecord(byte[] Parm);

    int OP_ReadRecords(byte[] Parm, byte[] outData, int[] outLen);

    int OP_ClearRecordFile(byte[] Parm);

    int OP_CommitTransaction(byte Option, byte[] outData, int[] outLen);

    int OP_CommitTransaction();

    int OP_AbortTransaction();

    int OP_CommitReaderID(byte[] Parm, byte[] outData, int[] outLen);

    int OP_Read_Sig(byte[] outData, int[] outLen);

    int OP_SetConfiguration(byte[] Parm);

    int OP_AuthenticateEV2NonFirst(byte[] Parm);

    int OP_AuthenticateEV2First(byte[] Parm);

    int OP_ChangeKey(byte[] Parm);

    int OP_ChangeKeyEV2(byte[] Parm);

    int OP_InitializeKeySet(byte[] Parm);

    int OP_FinalizeKeySet(byte[] Parm);

    int OP_RollKeySet(byte[] Parm);

    int OP_ChangeKeySettings(byte[] Parm);

    int OP_ChangeFileSettings(byte[] Parm);

    int OP_Felica_polling(byte Cmd[], byte outData[], int outLen[]);

    int OP_Felica_read(byte Cmd[], byte outData[], int outLen[]);

    int OP_Felica_write(byte Cmd[], byte outData[], int outLen[]);
}
