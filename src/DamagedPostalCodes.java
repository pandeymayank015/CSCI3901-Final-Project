public class DamagedPostalCodes{
    String postalCode, repairEstimate;
    DamagedPostalCodes(String postalCode, String repairEstimate){
        this.postalCode=postalCode;
        this.repairEstimate=repairEstimate;
    }
    String getPostalCode(){
        return postalCode;
    }
    String getRepairEstimate(){
        return repairEstimate;
    }
}
