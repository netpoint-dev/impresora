package com.common.demo.nfc;

import android.content.Context;

import com.common.apiutil.CommonException;
import com.common.apiutil.nfc.Nfc;


public class Demo implements NfcInterface {
    Nfc nfc;

    private final int _OK = 0;
    private final int _FALSE = -1;

    public Demo(Context context){
        nfc = new Nfc(context);
    }

    @Override
    protected void finalize() {
        OP_NFC_Device_Close();
    }

    @Override
    public int OP_NFC_Device_Open() {
        try {
            nfc.open();
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_NFC_Device_Close() {
        try {
            nfc.close();
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_NFC_Device_Activate(byte[] Parm, byte[] outData, int[] outLen) {
        try {
            byte[] data = nfc.activate(500);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CPU_GET_ATS(byte[] outData, int[] outLen)  {
        try {
            byte[] data = nfc.cpu_get_ats();
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;

        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_activateEv2ForNewCard() {
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = {0x00,0x00,0x00};

        try {
            nfc.SelectApplication(AID,AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.md_auth((byte) 0x00,(byte) 0x00,key);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;//no 2K3DES, shall is AES128
        }
        try {
            //nfc.ChangeKeyEv1((byte)0x80, key, key.length);
            nfc.md_change_key((byte)0x80, key);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_getFreeMem(byte[] outData, int[] outLen) {
        if ((outData==null) || (outLen==null))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = {0x00,0x00,0x00};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.FreeMem();
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;

        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_Format() {
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = {0x00,0x00,0x00};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.Format();
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_GetVersion(byte[] outData, int[] outLen) {
        if ((outData==null) || (outLen==null))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = {0x00,0x00,0x00};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetVersion();
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;

        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }

    }

    @Override
    public int OP_GetCardUID(byte[] outData, int[] outLen) {
        if ((outData==null) || (outLen==null))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = {0x00,0x00,0x00};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetCardUID();
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_GetKeySettings(byte[] AID, byte[] outData, int[] outLen) {
        if ((AID==null) || (outData==null) || (outLen==null) || (AID.length != 3))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetKeySettings();
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_GetKeyVersion(byte[] Parm, byte[] outData, int[] outLen) {
        if ((Parm==null) || (outData==null) || (outLen==null) || (Parm.length != 5))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = {Parm[0],Parm[1],Parm[2]};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetKeyVersion(Parm[3], Parm[4]);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CreateApplication(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 10))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID0 = {0x00,0x00,0x00};
        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);

        try {
            nfc.SelectApplication(AID0, AID0.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.CreateApplication(AID, AID.length, Parm[3], Parm[4], Parm[5], Parm[6], Parm[7], Parm[8], Parm[9]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_DeleteApplication(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 5))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID0 = {0x00,0x00,0x00};
        byte KeyNo = Parm[0];
        byte[] AID1 = {Parm[1],Parm[2],Parm[3]};
        byte AppType = Parm[4];

        try {
            nfc.SelectApplication(AID0, AID0.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, KeyNo, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.DeleteApplication(AID1, AID1.length, AppType);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CreateDelegatedApplication(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 15))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID0 = {0x00,0x00,0x00};
        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        byte[] DAMSlotNo = {Parm[3],Parm[4]};
        byte DAMSlotVersion = Parm[5];
        byte[] QuotaLimit = {Parm[6],Parm[7]};

        try {
            nfc.SelectApplication(AID0, AID0.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x10, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.CreateDelegatedApplication(AID, AID.length, DAMSlotNo, DAMSlotNo.length, DAMSlotVersion,
                    QuotaLimit, QuotaLimit.length, Parm[8], Parm[9], Parm[10], Parm[11], Parm[12], Parm[13], Parm[14]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_SelectApplication(byte[] AID) {
        if (AID==null)
            return -1;
        int rc = -1;

        if(AID.length == 3) {
            try {
                nfc.SelectApplication(AID, AID.length);
                return _OK;
            } catch (CommonException e) {
                e.printStackTrace();
                return _FALSE;
            }
        }
        else if(AID.length == 6) {
            byte[] AID1 = {AID[0],AID[1],AID[2]};
            byte[] AID2 = {AID[3],AID[4],AID[5]};
            try {
                nfc.SelectApplication2(AID1, AID1.length, AID2, AID2.length);
                return _OK;
            } catch (CommonException e) {
                e.printStackTrace();
                return _FALSE;
            }
        }else {
            return rc;
        }
    }

    @Override
    public int OP_GetApplicationIDs(byte[] outData, int[] outLen) {
        if ((outData==null) || (outLen==null))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = {0x00,0x00,0x00};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetApplicationIDs();
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_GetDelegatedInfo(byte[] DAMSlotNo, byte[] outData, int[] outLen) {
        if((DAMSlotNo==null) || (outData==null) || (outLen==null) || (DAMSlotNo.length != 2)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = new byte[3];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetDelegatedInfo(DAMSlotNo, DAMSlotNo.length);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CreateStdDataFile(byte[] Parm) {
        if ((Parm==null) || (Parm.length < 10))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte[] FileParm = new byte[Parm.length-3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length, FileParm, 0, FileParm.length);
        byte FileNo = FileParm[0];
        byte FileOption = FileParm[1];
        byte[] AccessRights = new byte[2];
        System.arraycopy(FileParm, 2, AccessRights, 0, AccessRights.length);
        byte[] FileSize = new byte[3];
        System.arraycopy(FileParm, 2+AccessRights.length, FileSize, 0, FileSize.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.CreateStdDataFile(FileNo, FileOption, AccessRights, AccessRights.length, FileSize, FileSize.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }

        if(Parm.length == 17){
            //EV3 TEST
            byte FileNoEv3 = Parm[10];
            byte FileOptionEv3 = Parm[11];
            byte[] AccessRightsEv3 = {Parm[12], Parm[13]};
            byte[] FileSizeEv3 = {Parm[14], Parm[15], Parm[16]};
            try {
                nfc.CreateStdDataFile(FileNoEv3, FileOptionEv3, AccessRightsEv3,
                        AccessRightsEv3.length, FileSizeEv3, FileSizeEv3.length);
            } catch (CommonException e) {
                e.printStackTrace();
                return _FALSE;
            }
        }
        return _OK;
    }

    @Override
    public int OP_CreateBackupDataFile(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 10))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte[] FileParm = new byte[Parm.length-3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length, FileParm, 0, FileParm.length);
        byte FileNo = FileParm[0];
        byte FileOption = FileParm[1];
        byte[] AccessRights = new byte[2];
        System.arraycopy(FileParm, 2, AccessRights, 0, AccessRights.length);
        byte[] FileSize = new byte[3];
        System.arraycopy(FileParm, 2+AccessRights.length, FileSize, 0, FileSize.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.CreateBackupDataFile(FileNo, FileOption, AccessRights, AccessRights.length, FileSize, FileSize.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CreateValueFile(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 20))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte[] FileParm = new byte[Parm.length-3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte FileOption = FileParm[1];
        byte[] AccessRights = new byte[2];
        System.arraycopy(FileParm, 2, AccessRights, 0, AccessRights.length);
        byte[] LowerLimit = new byte[4];
        System.arraycopy(FileParm, 4, LowerLimit, 0, LowerLimit.length);
        byte[] UpperLimit = new byte[4];
        System.arraycopy(FileParm, 8, UpperLimit, 0, UpperLimit.length);
        byte[] Value = new byte[4];
        System.arraycopy(FileParm, 12, Value, 0, Value.length);
        byte LimitedCreditEnabled = FileParm[16];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.CreateValueFile(FileNo, FileOption, AccessRights, AccessRights.length, LowerLimit, LowerLimit.length,
                    UpperLimit, UpperLimit.length, Value, Value.length, LimitedCreditEnabled);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CreateLinearRecordFile(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 13))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte[] FileParm = new byte[Parm.length-3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte FileOption = FileParm[1];
        byte[] AccessRights = new byte[2];
        System.arraycopy(FileParm, 1+1, AccessRights, 0, AccessRights.length);
        byte[] RecordSize = new byte[3];
        System.arraycopy(FileParm, 1+1+AccessRights.length, RecordSize, 0, RecordSize.length);
        byte[] MaxNoOfRecs = new byte[3];
        System.arraycopy(FileParm, 1+1+AccessRights.length+RecordSize.length, MaxNoOfRecs, 0, MaxNoOfRecs.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.CreateLinearRecordFile(FileNo, FileOption, AccessRights, AccessRights.length,
                    RecordSize, RecordSize.length, MaxNoOfRecs, MaxNoOfRecs.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CreateCyclicRecordFile(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 13))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte[] FileParm = new byte[Parm.length-3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length, FileParm, 0, FileParm.length);
        byte FileNo = FileParm[0];
        byte FileOption = FileParm[1];
        byte[] AccessRights = new byte[2];
        System.arraycopy(FileParm, 1+1, AccessRights, 0, AccessRights.length);
        byte[] RecordSize = new byte[3];
        System.arraycopy(FileParm, 1+1+AccessRights.length, RecordSize, 0, RecordSize.length);
        byte[] MaxNoOfRecs = new byte[3];
        System.arraycopy(FileParm, 1+1+AccessRights.length+RecordSize.length, MaxNoOfRecs, 0, MaxNoOfRecs.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.CreateCyclicRecordFile(FileNo, FileOption, AccessRights, AccessRights.length,
                    RecordSize, RecordSize.length, MaxNoOfRecs, MaxNoOfRecs.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CreateTransactionMACFile(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 25))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte[] FileParm = new byte[Parm.length-3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length, FileParm, 0, FileParm.length);
        byte FileNo = FileParm[0];
        byte FileOption = FileParm[1];
        byte[] AccessRights = new byte[2];
        System.arraycopy(FileParm, 1+1, AccessRights, 0, AccessRights.length);

        byte TMKeyOption = FileParm[4];
        byte[] TMKey = new byte[16];
        System.arraycopy(FileParm, 5, TMKey, 0, TMKey.length);
        byte TMKeyVer = FileParm[21];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.CreateTransactionMACFile(FileNo, FileOption, AccessRights, AccessRights.length,
                    TMKeyOption, TMKey, TMKey.length, TMKeyVer);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_DeleteFile(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 4))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.DeleteFile(Parm[3]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_GetFileIDs(byte[] AID, byte[] outData, int[] outLen) {
        if((AID==null) || (outData==null) || (outLen==null) || (AID.length != 3)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetFileIDs();
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_GetFileSettings(byte[] Parm, byte[] outData, int[] outLen) {
        if((Parm==null) || (outData==null) || (outLen==null) || (Parm.length != 4)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = {Parm[0], Parm[1], Parm[2]};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetFileSettings(Parm[3]);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_GetFileCounters(byte[] Parm, byte[] outData, int[] outLen) {
        if((Parm==null) || (outData==null) || (outLen==null) || (Parm.length != 4)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = {Parm[0], Parm[1], Parm[2]};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetFileCounters(Parm[3]);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_WriteData(byte[] Parm) {
        if((Parm==null) || (Parm.length < 12)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte CommMode = Parm[3];
        byte[] FileParm = new byte[Parm.length-4];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length+1, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte[] Offset = {FileParm[1],FileParm[2],FileParm[3]};
        byte[] Length = {FileParm[4],FileParm[5],FileParm[6]};
        byte[] Data = new byte[FileParm.length-7];
        System.arraycopy(FileParm, 7, Data, 0, Data.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.WriteData(FileNo, Offset, Offset.length, Length, Length.length, CommMode, Data, Data.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_ReadData(byte[] Parm, byte[] outData, int[] outLen) {
        if((Parm==null) || (outData==null) || (outLen==null) || (Parm.length != 11)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte CommMode = Parm[3];
        byte[] FileParm = new byte[Parm.length-4];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length+1, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte[] Offset = {FileParm[1],FileParm[2],FileParm[3]};
        byte[] Length = {FileParm[4],FileParm[5],FileParm[6]};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.ReadData(FileNo, Offset, Offset.length, Length, Length.length, CommMode);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_Credit(byte[] Parm) {
        if((Parm==null) || (Parm.length != 9)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte CommMode = Parm[3];
        byte[] FileParm = new byte[Parm.length-4];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length+1, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte[] Data = new byte[FileParm.length-1];
        System.arraycopy(FileParm, 1, Data, 0, Data.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.Credit(FileNo, CommMode, Data, Data.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_LimitedCredit(byte[] Parm) {
        if((Parm==null) || (Parm.length != 9)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte CommMode = Parm[3];
        byte[] FileParm = new byte[Parm.length-4];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length+1, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte[] Data = new byte[FileParm.length-1];
        System.arraycopy(FileParm, 1, Data, 0, Data.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.LimitedCredit(FileNo, CommMode, Data, Data.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_Debit(byte[] Parm) {
        if((Parm==null) || (Parm.length != 9)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte CommMode = Parm[3];
        byte[] FileParm = new byte[Parm.length-4];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length+1, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte[] Data = new byte[FileParm.length-1];
        System.arraycopy(FileParm, 1, Data, 0, Data.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.Debit(FileNo, CommMode, Data, Data.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_GetValue(byte[] Parm, byte[] outData, int[] outLen) {
        if((Parm==null) || (outData==null) || (outLen==null) || (Parm.length != 5)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte CommMode = Parm[3];
        byte FileNo = Parm[4];
        System.arraycopy(Parm, 0, AID, 0, AID.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.GetValue(FileNo, CommMode);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);

            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_WriteRecord(byte[] Parm) {
        if((Parm==null) || (Parm.length < 12)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte CommMode = Parm[3];
        byte[] FileParm = new byte[Parm.length-4];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length+1, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte[] Offset = {FileParm[1],FileParm[2],FileParm[3]};
        byte[] Length = {FileParm[4],FileParm[5],FileParm[6]};
        byte[] Data = new byte[FileParm.length-7];
        System.arraycopy(FileParm, 7, Data, 0, Data.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.WriteRecord(FileNo, Offset, Offset.length, Length, Length.length, CommMode, Data, Data.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_UpdateRecord(byte[] Parm) {
        if((Parm==null) || (Parm.length < 15)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte CommMode = Parm[3];
        byte[] FileParm = new byte[Parm.length-4];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length+1, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte[] RecNo = {FileParm[1],FileParm[2],FileParm[3]};
        byte[] Offset = {FileParm[4],FileParm[5],FileParm[6]};
        byte[] Length = {FileParm[7],FileParm[8],FileParm[9]};
        byte[] Data = new byte[FileParm.length-10];
        System.arraycopy(FileParm, 7, Data, 0, Data.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.UpdateRecord(FileNo, RecNo, RecNo.length, Offset, Offset.length, Length, Length.length, CommMode, Data, Data.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_ReadRecords(byte[] Parm, byte[] outData, int[] outLen) {
        if((Parm==null) || (outData==null) || (outLen==null) || (Parm.length != 11)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        byte CommMode = Parm[3];
        byte[] FileParm = new byte[Parm.length-4];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        System.arraycopy(Parm, AID.length+1, FileParm, 0, FileParm.length);

        byte FileNo = FileParm[0];
        byte[] RecNo = {FileParm[1],FileParm[2],FileParm[3]};
        byte[] RecCount = {FileParm[4],FileParm[5],FileParm[6]};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.ReadRecords(FileNo, RecNo, RecNo.length, RecCount, RecCount.length, CommMode);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_ClearRecordFile(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 4))
            return -1;
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.ClearRecordFile(Parm[3]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CommitTransaction(byte Option, byte[] outData, int[] outLen) {
        if ((outData==null) || (outLen==null))
            return -1;
        int rc = -1;

        try {

            byte[] data = nfc.CommitTransaction2(Option);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CommitTransaction() {
        int rc = -1;

        try {
            nfc.CommitTransaction();
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    public int OP_AbortTransaction() {

        int rc = -1;

        /*first write/read backup files, final can test AbortTransaction cmd*/

        try {
            nfc.Credit((byte)0x03, (byte)0x01, new byte[]{(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00}, 4);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AbortTransaction();
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_CommitReaderID(byte[] Parm, byte[] outData, int[] outLen) {
        if((Parm==null) || (outData==null) || (outLen==null) || (Parm.length != 16))
            return -1;

        int rc = -1;

        try {

            byte[] data = nfc.CommitReaderID(Parm, Parm.length);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_Read_Sig(byte[] outData, int[] outLen) {
        if((outData==null) || (outLen==null)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];
        byte[] AID = new byte[3];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            byte[] data = nfc.ReadSig((byte)0x00);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_SetConfiguration(byte[] Parm) {
        if((Parm==null) || (Parm.length > 29)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        byte Option = Parm[3];
        byte[] Data = new byte[Parm.length-4];
        System.arraycopy(Parm, AID.length+1, Data, 0, Data.length);

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.SetConfiguration(Option, Data, Data.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_AuthenticateEV2NonFirst(byte[] Parm) {
        if ((Parm==null) || (Parm.length != 20))
            return -1;
        int rc = -1;
        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);

        byte[] key = new byte[16];
        System.arraycopy(Parm, 3, key, 0, key.length);
        byte KeyNo = Parm[19];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, KeyNo, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2NonFirst(key, key.length, KeyNo);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_AuthenticateEV2First(byte[] Parm) {
        if ((Parm==null) || (Parm.length < 21) || (Parm.length > 27))
            return -1;
        int rc = -1;
        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);

        byte[] key = new byte[16];
        System.arraycopy(Parm, 3, key, 0, key.length);
        byte KeyNo = Parm[19];
        byte LenCap = Parm[20];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }

        if(Parm.length == 21) {
            try {
                nfc.AuthenticateEV2First(key, key.length, KeyNo, LenCap);
                return _OK;
            } catch (CommonException e) {
                e.printStackTrace();
                return _FALSE;
            }
        }else {
            byte[]PcdCap2 = new byte[Parm.length - 21];
            System.arraycopy(Parm, 21, PcdCap2, 0, PcdCap2.length);
            try {
                nfc.AuthenticateEV2First2(key, key.length, KeyNo, LenCap, PcdCap2, PcdCap2.length);
                return _OK;
            } catch (CommonException e) {
                e.printStackTrace();
                return _FALSE;
            }
        }
    }

    @Override
    public int OP_ChangeKey(byte[] Parm) {
        if ((Parm == null) || ((Parm.length != 22) && (Parm.length != 38))) {
            return -1;
        }
        int rc = -1;

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        byte[] OldKey = new byte[16];
        byte isEqualGetKeyNo = Parm[3];
        byte KeyNo = Parm[4];
        byte KeyVer = Parm[5];
        byte[] NewKey = new byte[16];
        System.arraycopy(Parm, 6, NewKey, 0, NewKey.length);
        if(Parm.length == 38) {
            System.arraycopy(Parm, 6+NewKey.length, OldKey, 0, OldKey.length);
        }

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(OldKey, OldKey.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.ChangeKey(isEqualGetKeyNo, KeyNo, KeyVer, NewKey, NewKey.length, OldKey, OldKey.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        if(KeyNo == 0x10){
            //EV3 TEST
            KeyNo = 0x11;
            try {
                nfc.ChangeKey(isEqualGetKeyNo, KeyNo, KeyVer, NewKey, NewKey.length, OldKey, OldKey.length);
            } catch (CommonException e) {
                e.printStackTrace();
                return _FALSE;
            }
        }
        return _OK;
    }

    @Override
    public int OP_ChangeKeyEV2(byte[] Parm) {
        if ((Parm == null) || ((Parm.length != 23) && (Parm.length != 39))) {
            return -1;
        }
        int rc = -1;

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        byte[] OldKey = new byte[16];
        byte isEqualGetKeyNo = Parm[3];
        byte KeySetNo = Parm[4];
        byte KeyNo = Parm[5];
        byte KeyVer = Parm[6];
        byte[] NewKey = new byte[16];
        System.arraycopy(Parm, 7, NewKey, 0, NewKey.length);
        if(Parm.length == 39) {
            System.arraycopy(Parm, 7+NewKey.length, OldKey, 0, OldKey.length);
        }

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(OldKey, OldKey.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.ChangeKeyEV2(isEqualGetKeyNo, KeySetNo, KeyNo, KeyVer, NewKey, NewKey.length, OldKey, OldKey.length);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_InitializeKeySet(byte[] Parm) {
        if((Parm==null) || (Parm.length != 5)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        byte KeySetNo = Parm[3];
        byte KeySetType = Parm[4];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.InitializeKeySet(KeySetNo, KeySetType);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_FinalizeKeySet(byte[] Parm) {
        if((Parm==null) || (Parm.length != 5)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        byte KeySetNo = Parm[3];
        byte KeySetVersion = Parm[4];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.FinalizeKeySet(KeySetNo, KeySetVersion);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_RollKeySet(byte[] Parm) {
        if((Parm==null) || (Parm.length != 4)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        byte KeySetNo = Parm[3];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.RollKeySet(KeySetNo);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_ChangeKeySettings(byte[] Parm) {
        if((Parm==null) || (Parm.length != 4)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        byte KeySetting = Parm[3];

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.ChangeKeySettings(KeySetting);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_ChangeFileSettings(byte[] Parm) {
        if((Parm==null) || (Parm.length<7)) {
            return -1;
        }
        int rc = -1;
        byte[] key = new byte[16];

        byte[] AID = new byte[3];
        System.arraycopy(Parm, 0, AID, 0, AID.length);
        byte FileNo = Parm[3];
        byte FileOption = Parm[4];
        byte[] AccessRights = {Parm[5], Parm[6]};

        try {
            nfc.SelectApplication(AID, AID.length);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        try {
            nfc.AuthenticateEV2First(key, key.length, (byte)0x00, (byte)0x00);
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
        if(Parm.length > 7){
            byte[] Option = new byte[Parm.length-7];
            System.arraycopy(Parm, 7, Option, 0, Option.length);
            try {
                nfc.ChangeFileSettings2(FileNo, FileOption, AccessRights, AccessRights.length, Option, Option.length);
                return _OK;
            } catch (CommonException e) {
                e.printStackTrace();
                return _FALSE;
            }
        }else{
            try {
                nfc.ChangeFileSettings(FileNo, FileOption, AccessRights, AccessRights.length);
                return _OK;
            } catch (CommonException e) {
                e.printStackTrace();
                return _FALSE;
            }
        }
    }

    @Override
    public int OP_Felica_polling(byte Cmd[], byte outData[], int outLen[]) {
        int rc = -1;

        try {

            byte[] data = nfc.FelicaPolling(Cmd, Cmd.length);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_Felica_read(byte Cmd[], byte outData[], int outLen[]) {
        int rc = -1;

        try {

            byte[] data = nfc.FelicaRead(Cmd, Cmd.length);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }

    @Override
    public int OP_Felica_write(byte Cmd[], byte outData[], int outLen[]) {
        int rc = -1;

        try {

            byte[] data = nfc.FelicaWrite(Cmd, Cmd.length);
            outLen[0] = data.length;
            System.arraycopy(data, 0, outData,0, outLen[0]);
            return _OK;
        } catch (CommonException e) {
            e.printStackTrace();
            return _FALSE;
        }
    }
}
