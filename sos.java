import Format;
import java.io.PrintStream;
import jobtype;

public class sos {
    static final int MAXINT = 20000000;
    static final int Crintt = 1;
    static final int Diskintt = 2;
    static final int Drumintt = 3;
    static final int Troo = 4;
    static final int Svcc = 5;
    static final int MaxJTable = 50;
    static final int MaxDiffJobs = 30;
    static final int TrivCutoff = 100;
    static final int CrMaplmt = 21;
    static final int EndOfDay = 900000;
    static boolean trace;
    static boolean err;
    static boolean err13;
    static int Clock;
    static int[] Next;
    static int[] Parms;
    static int jti;
    static int cmi;
    static int[] Action;
    static jobtype[] JobTable;
    static int[][] CoreMap;
    static double DrumUtil;
    static double DiskUtil;
    static double CpuUtil;
    static double CoreUtil;
    static double DelCntr;
    static double DilCntr;
    static int JobCntr;
    static int TermCntr;
    static double AvgDil;
    static double AvgResponse;
    static double AvgDisk;
    static int[] SizeDist;
    static int[] MCpuTimeDist;
    static int[] TCpuTimeDist;
    static int[] PriorDist;
    static int[][][] Times;
    static int[][] WhichSvc;
    static int[] DrmTimes;
    static int[] DskTimes;
    static int[] CrdTimes;
    static int CardTimesPtr;
    static int DiskTimesPtr;
    static int DrumTimesPtr;
    static boolean DiskBusy;
    static boolean DrumBusy;
    static int JobSwpdNo;
    static int CoreAddr;
    static int Sze;
    static boolean writ;
    static int JobServdIndex;
    static int DrmStTm;
    static int DskStTm;
    static double LastSnap;
    static double SpstInt;
    static double LstCrChk;
    static int CondCode;
    static String[] CondMess;
    static String[] errorarray;

    public sos() {
        Next = new int[6];
        Parms = new int[6];
        JobTable = new jobtype[52];
        CoreMap = new int[22][4];
        SizeDist = new int[30];
        MCpuTimeDist = new int[30];
        TCpuTimeDist = new int[30];
        PriorDist = new int[30];
        Times = new int[3][31][10];
        WhichSvc = new int[31][10];
        DrmTimes = new int[10];
        DskTimes = new int[10];
        CrdTimes = new int[10];
        Action = new int[1];
    }

    static int coreused() {
        int n = 0;
        for (int i = 1; i <= 20; ++i) {
            if (CoreMap[i][1] <= 0) continue;
            n = n + CoreMap[i][3] - CoreMap[i][2] + 1;
        }
        int n2 = n;
        return n2;
    }

    static void error(int n) {
        err = true;
        if (n == 13) {
            err13 = true;
        }
        System.out.println("\n\n\n*** Clock:  " + Clock + ", *** FATAL ERROR:  " + n + "\n");
        System.out.println(String.valueOf(errorarray[n]) + "\n");
        System.out.println("Current Value of Registers:\n\n\ta = " + Action[0]);
        System.out.print("\tp [1..5] = ");
        for (int i = 1; i <= 5; ++i) {
            System.out.print(" " + Parms[i] + "  ");
        }
        System.out.println("\n");
        sos.Statistics();
    }

    static void FindNextEvent() {
        int n;
        int n2 = 0;
        int n3 = 20000000;
        for (n = 1; n <= 3; ++n) {
            if (n3 <= Next[n]) continue;
            n2 = n;
            n3 = Next[n];
        }
        if (Action[0] == 2) {
            for (n = 5; n >= 4; --n) {
                if (n3 < Next[n] + Clock) continue;
                n2 = n;
                n3 = Next[n] + Clock;
            }
            sos.JobTable[sos.jti].CpuTimeUsed = sos.JobTable[sos.jti].CpuTimeUsed + n3 - Clock;
            CpuUtil = CpuUtil + (double)n3 - (double)Clock;
        }
        Clock = n3;
        sos.Action[0] = n2;
    }

    static void GenCrint() {
        int n;
        for (n = 1; !(n > 50 || sos.JobTable[n].Overwrite); ++n) {
        }
        if (n > 50) {
            sos.error(4);
        }
        if (!err) {
            int n2 = ++JobCntr % 30;
            sos.JobTable[n].JobNo = JobCntr;
            sos.JobTable[n].Size = SizeDist[n2];
            sos.JobTable[n].StartTime = Clock;
            sos.JobTable[n].CpuTimeUsed = 0;
            sos.JobTable[n].MaxCpuTime = MCpuTimeDist[n2];
            sos.JobTable[n].TermCpuTime = TCpuTimeDist[n2];
            sos.JobTable[n].NextSvc = 0;
            sos.JobTable[n].IOPending = 0;
            sos.JobTable[n].IOComp = 0;
            sos.JobTable[n].Priority = PriorDist[n2];
            sos.JobTable[n].JobType = sos.JobTable[n].MaxCpuTime < 100 || sos.JobTable[n].TermCpuTime < 100 ? 1 : 2;
            sos.JobTable[n].Blocked = false;
            sos.JobTable[n].Latched = false;
            sos.JobTable[n].InCore = false;
            sos.JobTable[n].Terminated = false;
            sos.JobTable[n].Overwrite = false;
            sos.Parms[1] = sos.JobTable[n].JobNo;
            sos.Parms[2] = sos.JobTable[n].Priority;
            sos.Parms[3] = sos.JobTable[n].Size;
            sos.Parms[4] = sos.JobTable[n].MaxCpuTime;
            sos.Parms[5] = Clock;
            int n3 = ++CardTimesPtr % 10;
            sos.Next[1] = CrdTimes[n3] + Clock;
            if (trace) {
                System.out.print("*** Clock:  " + Parms[5]);
                System.out.print(", Job " + Parms[1] + " Arriving ");
                System.out.print("Size:  " + Parms[3]);
                System.out.println(" Priority:  " + Parms[2]);
                System.out.println(" Max CPU Time:  " + Parms[4] + "\n");
            }
            if (sos.JobTable[n].MaxCpuTime <= 0 || sos.JobTable[n].Size <= 0 || sos.JobTable[n].Size > 100) {
                if (trace) {
                    System.out.println(" But job deleted due to max cpu time or size.");
                }
                sos.JobTable[n].Overwrite = true;
                DelCntr+=1.0;
            }
        }
    }

    static void GenDrmint() {
        if (trace) {
            System.out.print("*** Clock:  " + Clock + ", Swap ");
            if (writ) {
                System.out.print("out");
            } else {
                System.out.print("in");
            }
            System.out.println(" completed for job " + JobSwpdNo);
        }
        if (!writ) {
            for (int i = 1; i < 50 && sos.JobTable[i].JobNo != JobSwpdNo; ++i) {
            }
            sos.JobTable[i].InCore = true;
            int n = sos.coreused();
            CoreUtil+=(double)n * ((double)Clock - LstCrChk);
            LstCrChk = Clock;
            sos.CoreMap[sos.cmi][1] = JobSwpdNo;
            sos.CoreMap[sos.cmi][2] = CoreAddr;
            sos.CoreMap[sos.cmi][3] = CoreAddr + Sze - 1;
            sos.PutCoreMap();
        }
        DrumBusy = false;
        DrumUtil = DrumUtil + (double)Clock - (double)DrmStTm;
        sos.Next[3] = 1800000;
        sos.Action[0] = 3;
        sos.Parms[5] = Clock;
    }

    static void GenDskint() {
        int n = JobServdIndex;
        if (trace) {
            System.out.print("*** Clock:  " + Clock + ", IO Completion");
            System.out.println(" for job " + sos.JobTable[n].JobNo);
        }
        DiskBusy = false;
        sos.JobTable[n].Latched = false;
        if ((double)sos.JobTable[n].IOPending == 1.0) {
            sos.JobTable[n].Blocked = false;
        }
        --sos.JobTable[n].IOPending;
        ++sos.JobTable[n].IOComp;
        DiskUtil = DiskUtil + (double)Clock - (double)DskStTm;
        sos.Next[2] = 1800000;
        sos.Action[0] = 2;
        sos.Parms[5] = Clock;
        if (sos.JobTable[n].Terminated && (double)sos.JobTable[n].IOPending == 0.0) {
            int n2;
            int n3 = sos.coreused();
            CoreUtil+=(double)n3 * ((double)Clock - LstCrChk);
            LstCrChk = Clock;
            for (n2 = 1; n2 <= 21 && sos.JobTable[n].JobNo != CoreMap[n2][1]; ++n2) {
            }
            if (n2 > 21) {
                sos.error(39);
            }
            if (!err) {
                for (int i = 1; i <= 3; ++i) {
                    sos.CoreMap[n2][i] = 0;
                }
                sos.JobTable[n].Overwrite = true;
                sos.JobTable[n].InCore = false;
            }
        }
    }

    /*
     * Exception decompiling
     */
    static void GenSvc() {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:436)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.replaceRawSwitches(SwitchReplacer.java:62)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:416)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:214)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:159)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:91)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:353)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:731)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:663)
        // org.benf.cfr.reader.Main.doClass(Main.java:45)
        // org.benf.cfr.reader.Main.main(Main.java:180)
        throw new IllegalStateException("Decompilation failed");
    }

    static void GenTro() {
        if (trace) {
            System.out.print("Clock:  " + Clock + ", time run out ");
            System.out.println("on Job " + sos.JobTable[sos.jti].JobNo);
        }
        if (sos.JobTable[sos.jti].CpuTimeUsed >= sos.JobTable[sos.jti].MaxCpuTime) {
            CondCode = 2;
            sos.SaveStatistics();
        }
        sos.Parms[5] = Clock;
    }

    static void Idle() {
        if (trace) {
            System.out.println("*** Clock:  " + Clock + ", executive idling");
        }
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 1; !(i > 50 || bl2); ++i) {
            if (!sos.JobTable[i].InCore) continue;
            bl = true;
            if (sos.JobTable[i].Blocked || sos.JobTable[i].Terminated) continue;
            bl2 = true;
        }
        if (bl2) {
            sos.error(15);
        }
        if (!(err || DiskBusy || !bl)) {
            sos.error(16);
        }
        if (!(err || DrumBusy || JobCntr <= TermCntr || bl)) {
            sos.error(17);
        }
    }

    static void init() {
        int n;
        int n2;
        int[] arrn = new int[10];
        sos.DrmTimes[0] = 11;
        sos.DrmTimes[1] = 17;
        sos.DrmTimes[2] = 21;
        sos.DrmTimes[3] = 19;
        sos.DrmTimes[4] = 15;
        sos.DrmTimes[5] = 23;
        sos.DrmTimes[6] = 25;
        sos.DrmTimes[7] = 13;
        sos.DrmTimes[8] = 29;
        sos.DrmTimes[9] = 27;
        sos.DskTimes[0] = 55;
        sos.DskTimes[1] = 85;
        sos.DskTimes[2] = 105;
        sos.DskTimes[3] = 95;
        sos.DskTimes[4] = 75;
        sos.DskTimes[5] = 115;
        sos.DskTimes[6] = 125;
        sos.DskTimes[7] = 65;
        sos.DskTimes[8] = 145;
        sos.DskTimes[9] = 135;
        sos.CrdTimes[0] = 2800;
        sos.CrdTimes[1] = 2600;
        sos.CrdTimes[2] = 3300;
        sos.CrdTimes[3] = 1400;
        sos.CrdTimes[4] = 30;
        sos.CrdTimes[5] = 10;
        sos.CrdTimes[6] = 19;
        sos.CrdTimes[7] = 2850;
        sos.CrdTimes[8] = 2740;
        sos.CrdTimes[9] = 4000;
        sos.SizeDist[0] = 15;
        sos.SizeDist[1] = 18;
        sos.SizeDist[2] = 25;
        sos.SizeDist[3] = 8;
        sos.SizeDist[4] = 10;
        sos.SizeDist[5] = 30;
        sos.SizeDist[6] = 47;
        sos.SizeDist[7] = 27;
        sos.SizeDist[8] = 10;
        sos.SizeDist[9] = 14;
        sos.SizeDist[10] = 30;
        sos.SizeDist[11] = 16;
        sos.SizeDist[12] = 19;
        sos.SizeDist[13] = 23;
        sos.SizeDist[14] = 5;
        sos.SizeDist[15] = 15;
        sos.SizeDist[16] = 6;
        sos.SizeDist[17] = 10;
        sos.SizeDist[18] = 8;
        sos.SizeDist[19] = 7;
        sos.SizeDist[20] = 17;
        sos.SizeDist[21] = 15;
        sos.SizeDist[22] = 40;
        sos.SizeDist[23] = 11;
        sos.SizeDist[24] = 14;
        sos.SizeDist[25] = 17;
        sos.SizeDist[26] = 21;
        sos.SizeDist[27] = 23;
        sos.SizeDist[28] = 5;
        sos.SizeDist[29] = 8;
        sos.MCpuTimeDist[0] = 14000;
        sos.MCpuTimeDist[1] = 23;
        sos.MCpuTimeDist[2] = 2500;
        sos.MCpuTimeDist[3] = 20;
        sos.MCpuTimeDist[4] = 3500;
        sos.MCpuTimeDist[5] = 14;
        sos.MCpuTimeDist[6] = 65000;
        sos.MCpuTimeDist[7] = 100;
        sos.MCpuTimeDist[8] = 10;
        sos.MCpuTimeDist[9] = 1500;
        sos.MCpuTimeDist[10] = 11;
        sos.MCpuTimeDist[11] = 10;
        sos.MCpuTimeDist[12] = 550;
        sos.MCpuTimeDist[13] = 1400;
        sos.MCpuTimeDist[14] = 17;
        sos.MCpuTimeDist[15] = 40000;
        sos.MCpuTimeDist[16] = 19;
        sos.MCpuTimeDist[17] = 1300;
        sos.MCpuTimeDist[18] = 15;
        sos.MCpuTimeDist[19] = 21;
        sos.MCpuTimeDist[20] = 131;
        sos.MCpuTimeDist[21] = 153;
        sos.MCpuTimeDist[22] = 1000;
        sos.MCpuTimeDist[23] = 32;
        sos.MCpuTimeDist[24] = 18;
        sos.MCpuTimeDist[25] = 5300;
        sos.MCpuTimeDist[26] = 62;
        sos.MCpuTimeDist[27] = 17;
        sos.MCpuTimeDist[28] = 7100;
        sos.MCpuTimeDist[29] = 15;
        for (n = 0; n <= 14; ++n) {
            sos.PriorDist[2 * n] = 1;
            sos.PriorDist[2 * n + 1] = 2;
        }
        sos.PriorDist[6] = 5;
        sos.PriorDist[7] = 1;
        sos.TCpuTimeDist[0] = 4000;
        sos.TCpuTimeDist[1] = 21;
        sos.TCpuTimeDist[2] = 2000;
        sos.TCpuTimeDist[3] = 20;
        sos.TCpuTimeDist[4] = 4000;
        sos.TCpuTimeDist[5] = 11;
        sos.TCpuTimeDist[6] = 50000;
        sos.TCpuTimeDist[7] = 90;
        sos.TCpuTimeDist[8] = 9;
        sos.TCpuTimeDist[9] = 100;
        sos.TCpuTimeDist[10] = 10;
        sos.TCpuTimeDist[11] = 12;
        sos.TCpuTimeDist[12] = 500;
        sos.TCpuTimeDist[13] = 1300;
        sos.TCpuTimeDist[14] = 15;
        sos.TCpuTimeDist[15] = 3000;
        sos.TCpuTimeDist[16] = 15;
        sos.TCpuTimeDist[17] = 1200;
        sos.TCpuTimeDist[18] = 13;
        sos.TCpuTimeDist[19] = 20;
        sos.TCpuTimeDist[20] = 130;
        sos.TCpuTimeDist[21] = 150;
        sos.TCpuTimeDist[22] = 900;
        sos.TCpuTimeDist[23] = 37;
        sos.TCpuTimeDist[24] = 20;
        sos.TCpuTimeDist[25] = 2500;
        sos.TCpuTimeDist[26] = 60;
        sos.TCpuTimeDist[27] = 14;
        sos.TCpuTimeDist[28] = 3000;
        sos.TCpuTimeDist[29] = 15;
        for (n = 0; n <= 30; ++n) {
            for (n2 = 0; n2 <= 9; ++n2) {
                sos.Times[1][n][n2] = 3 * (n2 + 1);
            }
        }
        arrn[1] = 4;
        arrn[2] = 8;
        arrn[3] = 11;
        arrn[4] = 12;
        arrn[5] = 17;
        arrn[6] = 21;
        arrn[7] = 24;
        arrn[8] = 27;
        arrn[9] = 30;
        for (n = 1; n <= 9; ++n) {
            for (n2 = 0; n2 <= 9; ++n2) {
                sos.Times[1][arrn[n]][n2] = 500 * (n2 + 1);
            }
        }
        for (n = 0; n <= 14; ++n) {
            for (n2 = 0; n2 <= 9; ++n2) {
                sos.Times[2][2 * n][n2] = 3 * (n2 + 1);
            }
            for (n2 = 0; n2 <= 9; ++n2) {
                sos.Times[2][2 * n + 1][n2] = 500 * (n2 + 1);
            }
        }
        for (n2 = 0; n2 <= 9; ++n2) {
            sos.Times[2][30][n2] = 3 * (n2 + 1);
        }
        for (n = 0; n <= 30; ++n) {
            for (n2 = 0; n2 <= 9; ++n2) {
                sos.WhichSvc[n][n2] = 2 - n2 % 2;
            }
        }
        for (n = 1; n <= 51; ++n) {
            sos.JobTable[n] = new jobtype();
            sos.JobTable[n].JobNo = 0;
            sos.JobTable[n].Size = 0;
            sos.JobTable[n].StartTime = 0.0;
            sos.JobTable[n].CpuTimeUsed = 0;
            sos.JobTable[n].MaxCpuTime = 0;
            sos.JobTable[n].TermCpuTime = 0;
            sos.JobTable[n].NextSvc = 0;
            sos.JobTable[n].IOPending = 0;
            sos.JobTable[n].IOComp = 0;
            sos.JobTable[n].Priority = 0;
            sos.JobTable[n].JobType = 0;
            sos.JobTable[n].Blocked = false;
            sos.JobTable[n].Latched = false;
            sos.JobTable[n].InCore = false;
            sos.JobTable[n].Terminated = false;
            sos.JobTable[n].Overwrite = true;
        }
        for (n = 1; n <= 20; ++n) {
            for (n2 = 1; n2 <= 3; ++n2) {
                sos.CoreMap[n][n2] = 0;
            }
        }
        sos.CoreMap[21][1] = -1;
        sos.CoreMap[21][2] = 100;
        sos.CoreMap[21][3] = 1000000000;
        Clock = 0;
        sos.Action[0] = 1;
        sos.Next[1] = 0;
        for (n = 2; n <= 5; ++n) {
            sos.Next[n] = 900001;
        }
        JobCntr = 0;
        DelCntr = 0.0;
        TermCntr = 0;
        DilCntr = 0.0;
        DrumTimesPtr = 0;
        DiskTimesPtr = 0;
        CardTimesPtr = 0;
        AvgDil = 0.0;
        AvgResponse = 0.0;
        DrumUtil = 0.0;
        DiskUtil = 0.0;
        CpuUtil = 0.0;
        CoreUtil = 0.0;
        LstCrChk = 0.0;
        AvgDisk = 0.0;
        for (n = 0; n <= 9; ++n) {
            AvgDisk+=(double)DskTimes[n];
        }
        AvgDisk/=10.0;
        DiskBusy = false;
        DrumBusy = false;
        trace = false;
        LastSnap = 0.0;
        SpstInt = 60000.0;
        System.out.println("\n\n\t\t\tOPERATING SYSTEM SIMULATION\n\n");
    }

    public static void offtrace() {
        trace = false;
    }

    public static void ontrace() {
        trace = true;
    }

    static void PutCoreMap() {
        int[] arrn = new int[100];
        if (trace) {
            int n;
            for (n = 0; n <= 99; ++n) {
                arrn[n] = 0;
            }
            for (n = 1; n <= 20; ++n) {
                if (CoreMap[n][1] <= 0) continue;
                for (int i = sos.CoreMap[n][2]; i <= CoreMap[n][3]; ++i) {
                    arrn[i] = CoreMap[n][1];
                }
            }
            System.out.println("\n\n\t\t\t\tCORE MAP\n");
            for (n = 0; n < 4; ++n) {
                System.out.print(" Partition Job   ");
            }
            System.out.println("\n");
            for (n = 0; n < 25; ++n) {
                if (n < 10) {
                    System.out.print(" ");
                }
                System.out.print("     " + n + "\t   " + arrn[n] + "\t      ");
                System.out.print(String.valueOf(n + 25) + "    " + arrn[n + 25] + "\t       ");
                System.out.print(String.valueOf(n + 50) + "    " + arrn[n + 50] + "  \t");
                System.out.println(String.valueOf(n + 75) + "    " + arrn[n + 75]);
            }
            System.out.println();
        }
    }

    static void Run() {
        int n;
        int n2 = 1;
        for (n = 1; n < 21 && (Parms[2] != CoreMap[n][2] || Parms[3] != CoreMap[n][3] - CoreMap[n][2] + 1); ++n) {
        }
        if (n >= 21) {
            sos.error(13);
        }
        if (!err) {
            while (n2 <= 50 && CoreMap[n][1] != sos.JobTable[n2].JobNo) {
                ++n2;
            }
        }
        if (!(err || n2 <= 50)) {
            sos.error(5);
            n2 = 1;
        }
        jti = n2;
        if (!(err || sos.JobTable[sos.jti].InCore)) {
            System.err.println("JOBNO:  " + sos.JobTable[sos.jti].JobNo);
            sos.error(6);
        }
        if (!err && sos.JobTable[sos.jti].Blocked) {
            System.err.println("JOBNO:  " + sos.JobTable[sos.jti].JobNo);
            sos.error(10);
        }
        if (!err && sos.JobTable[sos.jti].Terminated) {
            System.err.println("JOBNO:  " + sos.JobTable[sos.jti].JobNo);
            sos.error(11);
        }
        if (!(err || Parms[4] + sos.JobTable[sos.jti].CpuTimeUsed <= sos.JobTable[sos.jti].MaxCpuTime)) {
            System.err.println("JOBNO:  " + sos.JobTable[sos.jti].JobNo);
            sos.error(12);
        }
        if (!(err || Parms[4] > 0)) {
            System.err.println("JOBNO:  " + sos.JobTable[sos.jti].JobNo);
            sos.error(14);
        }
        if (!err) {
            sos.Next[4] = Parms[4];
            int n3 = sos.JobTable[sos.jti].JobType;
            int n4 = 31;
            int n5 = jti % n4;
            int n6 = Times[n3][n5][9];
            int n7 = sos.JobTable[sos.jti].NextSvc;
            int n8 = n7 / 10;
            int n9 = n7 % 10;
            int n10 = Times[n3][n5][n9];
            int n11 = n6 * n8 + n10;
            int n12 = sos.JobTable[sos.jti].TermCpuTime;
            sos.Next[5] = n11 < n12 ? n11 - sos.JobTable[sos.jti].CpuTimeUsed : n12 - sos.JobTable[sos.jti].CpuTimeUsed;
            if (trace) {
                System.out.print("*** Clock:  " + Clock + ", ");
                System.out.print("Job " + sos.JobTable[sos.jti].JobNo + " ");
                System.out.print("running size:  " + Parms[3]);
                System.out.println(" Priority:  " + sos.JobTable[sos.jti].Priority);
                System.out.print(" Max CPU Time:  " + sos.JobTable[sos.jti].MaxCpuTime + ", ");
                System.out.println("CPU time used:  " + sos.JobTable[sos.jti].CpuTimeUsed);
                System.out.println();
            }
        }
    }

    static void SaveStatistics() {
        int n;
        if (sos.JobTable[sos.jti].CpuTimeUsed > 100) {
            double d;
            double d2;
            double d3 = Times[sos.JobTable[sos.jti].JobType][jti % 31][0];
            double d4 = d3 > AvgDisk ? (double)sos.JobTable[sos.jti].CpuTimeUsed : (d2 - (d = (d2 = d3 + AvgDisk)) >= 0.5 ? (d3 + AvgDisk) * (double)(sos.JobTable[sos.jti].IOComp - 1) + d3 + 1.0 : (d3 + AvgDisk) * (double)(sos.JobTable[sos.jti].IOComp - 1) + d3);
            double d5 = ((double)Clock - sos.JobTable[sos.jti].StartTime) / d4;
            System.out.print("\n*** Clock:  " + Clock + ", ");
            System.out.print("Job " + sos.JobTable[sos.jti].JobNo + " ");
            System.out.println("terminated " + CondMess[CondCode]);
            sos.printf("Dilation:  %.2f ", d5);
            System.out.print("CPU time:  " + sos.JobTable[sos.jti].CpuTimeUsed);
            System.out.println("  # I/O operations completed:  " + sos.JobTable[sos.jti].IOComp);
            System.out.println(" # I/O operations pending:  " + sos.JobTable[sos.jti].IOPending);
            System.out.println();
            AvgDil+=d5;
            DilCntr+=1.0;
        } else {
            double d = (double)Clock - sos.JobTable[sos.jti].StartTime;
            AvgResponse+=d;
            System.out.print("\n*** Clock:  " + Clock + ", ");
            System.out.print("Job " + sos.JobTable[sos.jti].JobNo + " ");
            System.out.println("terminated " + CondMess[CondCode]);
            sos.printf(" Response Time:  %.0f ", d);
            System.out.print("CPU time:  " + sos.JobTable[sos.jti].CpuTimeUsed);
            System.out.println(" # I/O operations completed:  " + sos.JobTable[sos.jti].IOComp);
            System.out.println(" # I/O operations pending:  " + sos.JobTable[sos.jti].IOPending);
            System.out.println();
        }
        ++TermCntr;
        for (n = 1; n < 21 && sos.JobTable[sos.jti].JobNo != CoreMap[n][1]; ++n) {
        }
        if (n == 21) {
            sos.error(37);
        }
        if (!err) {
            if (sos.JobTable[sos.jti].IOPending != 0) {
                sos.JobTable[sos.jti].Terminated = true;
            } else {
                sos.JobTable[sos.jti].Overwrite = true;
                sos.JobTable[sos.jti].InCore = false;
                sos.JobTable[sos.jti].Terminated = true;
                double d = sos.coreused();
                CoreUtil+=d * ((double)Clock - LstCrChk);
                LstCrChk = Clock;
                for (int i = 1; i <= 3; ++i) {
                    sos.CoreMap[n][i] = 0;
                }
            }
            sos.PutCoreMap();
        }
    }

    /*
     * Exception decompiling
     */
    public static void main(String[] var0) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:436)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.replaceRawSwitches(SwitchReplacer.java:62)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:416)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:214)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:159)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:91)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:353)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:731)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:663)
        // org.benf.cfr.reader.Main.doClass(Main.java:45)
        // org.benf.cfr.reader.Main.main(Main.java:180)
        throw new IllegalStateException("Decompilation failed");
    }

    public static void siodisk(int n) {
        int n2 = 1;
        if (trace) {
            System.out.println("*** Clock:  " + Clock + ", Job " + n + " I/O started");
        }
        while (n2 <= 50 && sos.JobTable[n2].JobNo != n) {
            ++n2;
        }
        if (n2 > 50) {
            sos.error(18);
            n2 = 1;
        }
        if (!(err || sos.JobTable[n2].InCore)) {
            sos.error(19);
        }
        if (!err && sos.JobTable[n2].Overwrite) {
            sos.error(20);
        }
        if (!err && DiskBusy) {
            sos.error(21);
        }
        if (!(err || sos.JobTable[n2].IOPending != 0)) {
            sos.error(22);
        }
        if (!err) {
            DskStTm = Clock;
            sos.Next[2] = Clock + DskTimes[++DiskTimesPtr % 10];
            sos.JobTable[n2].Latched = true;
            JobServdIndex = n2;
            DiskBusy = true;
        }
    }

    public static void siodrum(int n, int n2, int n3, int n4) {
        int n5 = 1;
        JobSwpdNo = n;
        Sze = n2;
        CoreAddr = n3;
        writ = n4 == 1;
        if (trace) {
            System.out.print("*** Clock:  " + Clock + ", Job " + JobSwpdNo);
            if (writ) {
                System.out.print(" swapout started.  ");
            } else {
                System.out.print(" swapin started.  ");
            }
            System.out.println("Size:  " + Sze);
            System.out.println(" Starting address:  " + CoreAddr + "\n");
        }
        sos.PutCoreMap();
        if (DrumBusy) {
            sos.error(23);
        }
        if (!err) {
            while (n5 <= 50 && sos.JobTable[n5].JobNo != JobSwpdNo) {
                ++n5;
            }
        }
        if (n5 > 50) {
            sos.error(24);
            n5 = 1;
        }
        if (!(err || sos.JobTable[n5].Size == n2)) {
            sos.error(25);
        }
        if (!(err || Sze != 0)) {
            sos.error(36);
        }
        if (!err && sos.JobTable[n5].Overwrite) {
            sos.error(26);
        }
        double d = Sze + CoreAddr - 1;
        if (writ) {
            int n6;
            if (!(err || sos.JobTable[n5].InCore)) {
                sos.error(27);
            }
            if (!err && sos.JobTable[n5].Latched) {
                sos.error(28);
            }
            if (!err) {
                for (n6 = 1; n6 <= 21 && CoreMap[n6][1] != JobSwpdNo; ++n6) {
                }
            }
            if ((sos.cmi = n6) > 21) {
                sos.error(29);
                cmi = 1;
            }
            if (!(err || CoreAddr == CoreMap[cmi][2])) {
                sos.error(30);
            }
            if (!(err || d == (double)CoreMap[cmi][3])) {
                sos.error(31);
            }
            if (!err) {
                sos.JobTable[n5].InCore = false;
                double d2 = sos.coreused();
                CoreUtil+=d2 * ((double)Clock - LstCrChk);
                LstCrChk = Clock;
                sos.CoreMap[sos.cmi][1] = 0;
                sos.CoreMap[sos.cmi][2] = 0;
                sos.CoreMap[sos.cmi][3] = 0;
                sos.PutCoreMap();
            }
        } else {
            int n7;
            if (!err && sos.JobTable[n5].InCore) {
                sos.error(32);
            }
            if (!(err || CoreAddr >= 0)) {
                sos.error(33);
            }
            if (!err) {
                for (n7 = 1; n7 <= 21; ++n7) {
                    if (CoreMap[n7][1] == 0 || CoreAddr > CoreMap[n7][3] || d < (double)CoreMap[n7][2]) continue;
                    sos.error(34);
                }
            }
            if (!err) {
                for (n7 = 1; n7 <= 21 && CoreMap[n7][1] != 0; ++n7) {
                }
            }
            if ((sos.cmi = n7) > 21) {
                sos.error(35);
            }
        }
        DrumBusy = true;
        DrmStTm = Clock;
        sos.Next[3] = DrmTimes[++DrumTimesPtr % 10] + Clock;
    }

    static void SnapShot() {
        int n;
        LastSnap = Clock;
        System.out.println("\n\n\n * * * SYSTEM STATUS AT " + Clock + " * * *");
        System.out.println(" ===================================\n");
        if (!(Action[0] != 2 || err13)) {
            for (n = 1; n <= 21 && (Parms[2] != CoreMap[n][2] || Parms[3] != CoreMap[n][3] - CoreMap[n][2] + 1); ++n) {
            }
            if (n >= 21) {
                sos.error(13);
            }
            if (!err13) {
                System.out.println(" CPU:  job #" + CoreMap[n][1] + " running");
            }
        } else {
            System.out.println(" CPU:  idle");
        }
        if (!err13) {
            if (DiskBusy) {
                System.out.print(" Disk running for job ");
                System.out.print(sos.JobTable[sos.JobServdIndex].JobNo);
                System.out.println(" since " + DskStTm);
            } else {
                System.out.println(" Disk:  idle");
            }
            if (DrumBusy) {
                System.out.print("Drum:  swapping job " + JobSwpdNo);
                if (writ) {
                    System.out.println(" out since " + DrmStTm);
                } else {
                    System.out.println(" in since " + DrmStTm);
                }
            } else {
                System.out.println(" Drum:  idle");
            }
            int n2 = sos.coreused();
            System.out.println("Memory:  " + n2 + " K words in use");
            System.out.print("Average dilation:  ");
            if (DilCntr == 0.0) {
                System.out.println("0.00");
            } else {
                sos.printf("%.2f\n", AvgDil / DilCntr);
            }
            System.out.print("Average Response time:  ");
            if ((double)TermCntr - DilCntr == 0.0) {
                System.out.println("0.00");
            } else {
                sos.printf("%.2f\n", AvgResponse / ((double)TermCntr - DilCntr));
            }
            boolean bl = trace;
            trace = true;
            sos.PutCoreMap();
            trace = bl;
            System.out.println("\n\n\t\t\tJOBTABLE\n");
            System.out.print("Job#  Size  Time CPUTime MaxCPU  I/O's ");
            System.out.println("Priority Blocked  Latched InCore Term");
            System.out.print("          Arrived  Used  Time   Pending");
            System.out.println("\n\n");
            for (n = 1; n <= 50; ++n) {
                if (sos.JobTable[n].Overwrite) continue;
                sos.printf("%4d  ", sos.JobTable[n].JobNo);
                sos.printf("%3d  ", sos.JobTable[n].Size);
                sos.printf("%6.0f ", sos.JobTable[n].StartTime);
                sos.printf("%6d ", sos.JobTable[n].CpuTimeUsed);
                sos.printf("%6d  ", sos.JobTable[n].MaxCpuTime);
                sos.printf("%3d  ", sos.JobTable[n].IOPending);
                sos.printf("    %d  ", sos.JobTable[n].Priority);
                if (sos.JobTable[n].Blocked) {
                    System.out.print("     yes");
                } else {
                    System.out.print("     no ");
                }
                if (sos.JobTable[n].Latched) {
                    System.out.print("     yes");
                } else {
                    System.out.print("     no ");
                }
                if (sos.JobTable[n].InCore) {
                    System.out.print("     yes");
                } else {
                    System.out.print("     no ");
                }
                if (sos.JobTable[n].Terminated) {
                    System.out.println("     yes");
                    continue;
                }
                System.out.println("     no ");
            }
            System.out.println("\n\n");
            if (Clock != 0) {
                System.out.println("\n\n");
                System.out.print(" Total jobs:  " + JobCntr + "\t");
                System.out.println("terminated:  " + TermCntr);
                sos.printf(" %% utilization   CPU:  %.2f", CpuUtil * 100.0 / (double)Clock);
                sos.printf("   disk:  %.2f", DiskUtil * 100.0 / (double)Clock);
                sos.printf("   drum:  %.2f", DrumUtil * 100.0 / (double)Clock);
                sos.printf("   memory:  %.2f", CoreUtil / (double)Clock);
            }
            System.out.println("\n\n");
        }
    }

    private static void Statistics() {
        System.out.println("\n\n                          FINAL STATISTICS");
        sos.SnapShot();
    }

    private static void printf(String string, int n) {
        Format.print(System.out, string, n);
    }

    private static void printf(String string, double d) {
        Format.print(System.out, string, d);
    }

    static {
        CondMess = new String[]{"ILLEGAL ERROR", "normally (terminate svc issued) ", "abnormally (max cpu time exceeded) "};
        errorarray = new String[]{"ILLEGAL ERROR", "/// MAIN ERROR ** INCORRECT VALUE OF ACTION SET BY FIND THE NEXT EVENT ///", "/// MAIN ERROR ** INCORRECT VALUE OF ACTION SET BY GEN ROUTINES ///", "*** MAIN ERROR ** INCORRECT VALUE OF ACTION RETURNED BY OS ***", "*** GENCRINT ERROR ** JOB TABLE FULL - OS PROCESSING JOBS TOO SLOWLY ***", "/// RUN ERROR ** JOB SPECIFIED DOES NOT EXIST IN JOB TABLE ///", "/// RUN ERROR ** JOB SPECIFIED IS NOT IN CORE ///", "/// RUN ERROR ** JOB SPECFIED NOT IN CORE-MISSING IN CORE MAP ///", "*** RUN ERROR ** INCORRECT START LOCATION IN CORE SPECIFIED FOR JOB ***", "*** RUN ERROR ** INCORRECT SIZE IN CORE SPECIFIED FOR JOB ***", "*** RUN ERROR ** JOB SPECIFIED TO RUN IS BLOCKED ***", "*** RUN ERROR ** JOB TERMINATED OR HAS EXCEEDED MAXIMUM CPU TIME ***", "*** RUN ERROR ** QUANTUM OF TIME FOR JOB SPECIFIED EXCEEDED MAXIMUM CPU TIME ***", "*** RUN ERROR ** STARTING ADDRESS OR LENGTH SPECIFIED IS INCORRECT ***", "*** RUN ERROR ** QUANTUM SPECIFIED IS NEGATIVE OR ZERO ***", "*** IDLE ERROR ** UNBLOCKED JOBS EXISTS IN CORE ***", "*** IDLE ERROR ** BLOCKED JOBS IN CORE BUT DISK IDLE ***", "*** IDLE ERROR ** OS FAILS TO SWAP JOBS FROM DRUM INTO EMPTY CORE ***", "*** SIODISK ERROR ** JOB SPECIFIED DOES NOT EXIST ***", "*** SIODISK ERROR ** JOB SPECIFIED NOT IN CORE ***", "*** SIODISK ERROR ** JOB SPECIFIED HAS TERMINATED ***", "*** SIODISK ERROR ** DISK IS BUSY ***", "*** SIODISK ERROR ** JOB HAS NO IO PENDING ***", "*** SIODRUM ERROR ** DRUM IS BUSY ***", "*** SIODRUM ERROR ** JOB SPECIFIED DOES NOT EXIST ***", "*** SIODRUM ERROR ** SIZE OF JOB SPECIFIED IS INCORRECT ***", "*** SIODRUM ERROR ** JOB SPECIFIED HAS TERMINATED ***", "*** SIODRUM ERROR ** JOB SPECIFIED NOT IN CORE ***", "*** SIODRUM ERROR ** JOB SPECIFIED IS LATCHED - IT CANNOT BE SWAPPED ***", "/// SIODRUM ERROR ** JOB SPECIFIED NOT IN CORE - DOES NOT EXIST IN CORE MAP ///", "*** SIODRUM ERROR ** START LOCATION OF JOB SPECIFIED IS INCORRECT ***", "/// SIODRUM ERROR ** SIZE OF JOB SPECIFIED IS INCORRECT ///", "*** SIODRUM ERROR ** JOB SPECIFIED IS ALREADY IN CORE - NO NEED TO SWAP IN ***", "*** SIODRUM ERROR ** START LOCATION OF JOB SPECIFIED IS NEGATIVE ***", "*** SIODRUM ERROR ** CORE ADDRESSES OF JOB SPECIFIED OVERLAP OTHER JOBS ***", "/// SIODRUM ERROR ** CORE MAP FULL - NO ROOM IN CORE ///", "*** SIODRUM ERROR ** ATTEMPT TO SWAP IN JOB WITH SIZE = 0 ***", "/// SAVESTATISTICS ERROR ** JOB SPECIFIED NOT IN CORE MAP ///", "/// GENSVC ERROR ** INCORRECT SWITCH VALUE ///", "/// GENDSKINT ERROR ** CAN'T FIND JOB IN CORE MAP IN ORDER TO DELETE ///", "           "};
    }
}