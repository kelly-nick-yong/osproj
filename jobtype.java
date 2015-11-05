//comments
class jobtype {
    int JobNo;
    int Size;
    double StartTime;
    int CpuTimeUsed;
    int MaxCpuTime;
    int TermCpuTime;
    int NextSvc;
    int IOPending;
    int IOComp;
    int Priority;
    int JobType;
    boolean Blocked;
    boolean Latched;
    boolean InCore;
    boolean Terminated;
    boolean Overwrite;

    jobtype() {
    }

    boolean Blocked() {
        return this.Blocked;
    }

    void Blocked(boolean bl) {
        this.Blocked = bl;
    }

    int CpuTimeUsed() {
        return this.CpuTimeUsed;
    }

    void CpuTimeUsed(int n) {
        this.CpuTimeUsed = n;
    }

    int IOComp() {
        return this.IOComp;
    }

    void IOComp(int n) {
        this.IOComp = n;
    }

    int IOPending() {
        return this.IOPending;
    }

    void IOPending(int n) {
        this.IOPending = n;
    }

    boolean InCore() {
        return this.InCore;
    }

    void InCore(boolean bl) {
        this.InCore = bl;
    }

    int JobNo() {
        return this.JobNo;
    }

    void JobNo(int n) {
        this.JobNo = n;
    }

    int JobType() {
        return this.JobType;
    }

    void JobType(int n) {
        this.JobType = n;
    }

    boolean Latched() {
        return this.Latched;
    }

    void Latched(boolean bl) {
        this.Latched = bl;
    }

    int MaxCpuTime() {
        return this.MaxCpuTime;
    }

    void MaxCpuTime(int n) {
        this.MaxCpuTime = n;
    }

    int NextSvc() {
        return this.NextSvc;
    }

    void NextSvc(int n) {
        this.NextSvc = n;
    }

    boolean Overwrite() {
        return this.Overwrite;
    }

    void Overwrite(boolean bl) {
        this.Overwrite = bl;
    }

    int Priority() {
        return this.Priority();
    }

    void Priority(int n) {
        this.Priority = n;
    }

    int Size() {
        return this.Size;
    }

    void Size(int n) {
        this.Size = n;
    }

    double StartTime() {
        return this.StartTime;
    }

    void StartTime(double d) {
        this.StartTime = d;
    }

    int TermCpuTime() {
        return this.TermCpuTime;
    }

    void TermCpuTime(int n) {
        this.TermCpuTime = n;
    }

    boolean Terminated() {
        return this.Terminated;
    }

    void Terminated(boolean bl) {
        this.Terminated = bl;
    }
}