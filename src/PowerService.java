import java.sql.*;
import java.util.*;
public class PowerService {
    private static Connection connect = null;
    Statement statement = null;

    PowerService(){
        try {
            JdbcConnection j = new JdbcConnection();
            connect = j.createConnection();
        }
         catch (Exception e) {
             throw new RuntimeException(e);
        }
    }

    boolean addPostalCode ( String postalCode, int population, int area ) throws SQLException {
        try{
        statement = connect.createStatement();
        statement.execute("insert into AddPostalCode values('"+postalCode+"',"+ population+","+area+");");
        statement.close();
        return true;}
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    boolean addDistributionHub ( String hubIdentifier, Point location, Set servicedAreas ) throws SQLException {
        try{statement = connect.createStatement();
        Iterator value = servicedAreas.iterator();
        statement.execute("insert into AddDistributionHub (HubIdentifier,Location) values('" + hubIdentifier + "','" + location.loc() + "');");
        while (value.hasNext()) {
            statement.execute("insert into ServiceArea (HubIdentifier,servicedAreas) values('" + hubIdentifier + "','" + value.next() + "');");
        }
        statement.close();
        return true;}
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    void hubDamage (String hubIdentifier, float repairEstimate ) throws SQLException {
        try {
            statement = connect.createStatement();
            statement.execute("insert into HubDamage values('" + hubIdentifier + "'," + repairEstimate + ");");
            statement.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    void hubRepair( String hubIdentifier, String employeeId, float repairTime, boolean inService ) throws SQLException {
        /*Reporting method.
        Reports:
        1. Employee_id
        2. hub ID
        3. repair time    */
        try{
        statement = connect.createStatement();
        statement.execute("insert into HubRepair values('" + hubIdentifier + "','" + employeeId + "'," + repairTime + "," + inService + ");");
        statement.execute("update HubDamage\n" +
                "set RepairEstimate=RepairEstimate-"+repairTime+" where HubIdentifier in ('"+hubIdentifier+"');");
        statement.execute("UPDATE HubRepair INNER JOIN (SELECT HubRepair.HubIdentifier, RepairEstimate, InService\n" +
                "\tFROM\n" +
                "\tHubRepair\n" +
                "\tLEFT JOIN\n" +
                "\tHubDamage ON HubRepair.HubIdentifier = HubDamage.HubIdentifier) a\n" +
                "    ON HubRepair.HubIdentifier = a.HubIdentifier\n" +
                "    SET HubRepair.InService = true\n" +
                "    where HubRepair.HubIdentifier='"+hubIdentifier+"' and a.RepairEstimate=0;");

        statement.close();}
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    int getTotalPopulation() {

        int totalPop = 0;
        try {

            statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("select sum(Population) from AddPostalCode;\n");
            while (resultSet.next()){
                totalPop = resultSet.getInt("sum(Population)");}
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return totalPop;
    }
    int peopleOutOfService () {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("Unable to connect");
        }
        float a = 0;
        try {
            statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("select sum(a.populationServedByHub) as pos from (select distinct ServiceArea.HubIdentifier,  \n" +
                    "\t\t\t\tServiceArea.servicedAreas, \n" +
                    "\t\t\t\tAddPostalCode.Population , \n" +
                    "                HubRepair.Inservice,\n" +
                    "                c,\n" +
                    "                AddPostalCode.Population/c as populationServedByHub\n" +
                    "                from ServiceArea , HubRepair, AddPostalCode,\n" +
                    "                (select distinct servicedAreas, count(HubIdentifier) as c \n" +
                    "                from ServiceArea \n" +
                    "                group by servicedAreas) sub\n" +
                    " where \n" +
                    "    ServiceArea.servicedAreas=AddPostalCode.PostalCode\n" +
                    "    and sub.servicedAreas=ServiceArea.servicedAreas\n" +
                    "\tand HubRepair.HubIdentifier= ServiceArea.HubIdentifier\n" +
                    "\tand HubRepair.InService=false) a;");
            while (resultSet.next()) {

                String str = resultSet.getString("pos");
                a = Float.parseFloat(str);
            }
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Math.round(a);
    }

    List<DamagedPostalCodes> mostDamagedPostalCodes (int limit ) throws SQLException {
        List<DamagedPostalCodes> mostDamagedPostalCodes = new ArrayList<>();

        try{
        statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("select  ServiceArea.servicedAreas, sum(HubDamage.RepairEstimate) as damaged from ServiceArea \n" +
                "left join HubDamage on ServiceArea.HubIdentifier=HubDamage.HubIdentifier \n" +
                "group by ServiceArea.servicedAreas order by damaged desc limit " + limit + ";");

        while (resultSet.next()) {
            DamagedPostalCodes d=new DamagedPostalCodes(resultSet.getString("servicedAreas"),
                    resultSet.getString("damaged"));
            mostDamagedPostalCodes.add(d);
        }
        resultSet.close();
        statement.close();}
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mostDamagedPostalCodes;
    }

    List<HubImpact> fixOrder ( int limit ) throws SQLException {
        List<HubImpact> fixOrder = new ArrayList<>();

try{
        statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT \n" +
                "  a.HubIdentifier,  SUM(a.populationServedByHub)/HubDamage.RepairEstimate AS impact\n" +
                "FROM\n" +
                "    (SELECT DISTINCT\n" +
                "        ServiceArea.HubIdentifier,\n" +
                "            ServiceArea.servicedAreas,\n" +
                "            AddPostalCode.Population,\n" +
                "            HubRepair.Inservice,\n" +
                "            c,\n" +
                "            AddPostalCode.Population / c AS populationServedByHub\n" +
                "    FROM\n" +
                "        ServiceArea, HubRepair, AddPostalCode, (SELECT DISTINCT\n" +
                "        servicedAreas, COUNT(HubIdentifier) AS c\n" +
                "    FROM\n" +
                "        ServiceArea\n" +
                "    GROUP BY servicedAreas) sub\n" +
                "    WHERE\n" +
                "        ServiceArea.servicedAreas = AddPostalCode.PostalCode AND HubRepair.InService = false\n" +
                "            AND sub.servicedAreas = ServiceArea.servicedAreas\n" +
                "            AND HubRepair.HubIdentifier = ServiceArea.HubIdentifier\n" +
                "             ) a , HubDamage where a.HubIdentifier=HubDamage.HubIdentifier \n" +
                "             group by a.HubIdentifier order by impact desc limit " + limit + " ;");
        HubImpact h;
        while (resultSet.next()) {
            h = new HubImpact(resultSet.getString("HubIdentifier"), resultSet.getString("impact"));
            fixOrder.add(h);
        }
        statement.close();}
    catch (SQLException e) {
        throw new RuntimeException(e);
    }
        return fixOrder;
    }

    List<Integer> rateOfServiceRestoration ( float increment ) throws SQLException {

        List<Integer> repairEstimate = new ArrayList<>();
        List<Integer> pop= new ArrayList<>();
        List<Integer> x=new ArrayList<>();
        int totalPop = getTotalPopulation();
        int poos= peopleOutOfService();
try{
        statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT \n" +
                "  a.HubIdentifier, SUM(a.populationServedByHub), SUM(a.populationServedByHub)/HubDamage.RepairEstimate AS pos, HubDamage.RepairEstimate\n" +
                "FROM\n" +
                "    (SELECT DISTINCT\n" +
                "        ServiceArea.HubIdentifier,\n" +
                "            ServiceArea.servicedAreas,\n" +
                "            AddPostalCode.Population,\n" +
                "            HubRepair.Inservice,\n" +
                "            c,\n" +
                "            AddPostalCode.Population / c AS populationServedByHub\n" +
                "    FROM\n" +
                "        ServiceArea, HubRepair, AddPostalCode, (SELECT DISTINCT\n" +
                "        servicedAreas, COUNT(HubIdentifier) AS c\n" +
                "    FROM\n" +
                "        ServiceArea\n" +
                "    GROUP BY servicedAreas) sub\n" +
                "    WHERE\n" +
                "\t\t\tServiceArea.servicedAreas = AddPostalCode.PostalCode\n" +
                "            AND HubRepair.InService = false\n" +
                "            AND sub.servicedAreas = ServiceArea.servicedAreas\n" +
                "            AND HubRepair.HubIdentifier = ServiceArea.HubIdentifier\n" +
                "             ) a , HubDamage where a.HubIdentifier=HubDamage.HubIdentifier \n" +
                "             group by a.HubIdentifier order by pos desc ;");


        while (resultSet.next()) {
            repairEstimate.add(resultSet.getInt("RepairEstimate"));
            pop.add(resultSet.getInt("SUM(a.populationServedByHub)"));
        }
        for (int i=0;i<repairEstimate.size();i++){
        }
        int i=0;
        int j=0;
        int peopleWithPower=totalPop-poos;
        int hr=0;
        while( i<=100){

            int percentPop=(totalPop*i)/100;
            if(percentPop>peopleWithPower){
                hr+=repairEstimate.get(j);
                peopleWithPower+=pop.get(j);
                if(repairEstimate.size()>j+1){
                    j++;
                }
            }
            x.add(hr);
            i= (int) (i+increment*100);
        }
        resultSet.close();
        statement.close();}
    catch (SQLException e) {
    throw new RuntimeException(e);
}
        return x;
    }

    List<HubImpact> repairPlan ( String startHub, int maxDistance, float maxTime ) throws SQLException {
        Map<Integer,Integer> coordinates=new HashMap<>();

        try {
            statement = connect.createStatement();
            statement.execute("select * from AddDistributionHub);");
            statement.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    List<String> underservedPostalByPopulation ( int limit ) throws SQLException {
        List<String> str;
        try{
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT DISTINCT\n" +
                "            ServiceArea.servicedAreas,\n" +
                "            AddPostalCode.Population,\n" +
                "            HubRepair.Inservice,\n" +
                "            c,\n" +
                "            c/AddPostalCode.Population  AS populationServedByHub\n" +
                "    FROM\n" +
                "        ServiceArea, HubRepair, AddPostalCode, (SELECT DISTINCT\n" +
                "        servicedAreas, COUNT(HubIdentifier) AS c\n" +
                "    FROM\n" +
                "        ServiceArea\n" +
                "    GROUP BY servicedAreas) sub\n" +
                "    WHERE\n" +
                "        ServiceArea.servicedAreas = AddPostalCode.PostalCode\n" +
                "            AND sub.servicedAreas = ServiceArea.servicedAreas\n" +
                "            AND HubRepair.HubIdentifier = ServiceArea.HubIdentifier\n" +
                "            AND HubRepair.InService = FALSE\n" +
                "            order by populationServedByHub desc limit " + limit + ";");
        str = new ArrayList<>();
        while (resultSet.next()) {
            str.add(resultSet.getString("servicedAreas"));
        } statement.close();}
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return str;
    }

    List<String> underservedPostalByArea (int limit ) throws SQLException {
        List<String> str;
        try{Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT DISTINCT\n" +
                "            ServiceArea.servicedAreas,\n" +
                "            AddPostalCode.Area,\n" +
                "            HubRepair.Inservice,\n" +
                "            c,\n" +
                "            c/AddPostalCode.Area AS AreaServedByHub\n" +
                "    FROM\n" +
                "        ServiceArea, HubRepair, AddPostalCode, (SELECT DISTINCT\n" +
                "        servicedAreas, COUNT(HubIdentifier) AS c\n" +
                "    FROM\n" +
                "        ServiceArea\n" +
                "    GROUP BY servicedAreas) sub\n" +
                "    WHERE\n" +
                "        ServiceArea.servicedAreas = AddPostalCode.PostalCode\n" +
                "            AND sub.servicedAreas = ServiceArea.servicedAreas\n" +
                "            AND HubRepair.HubIdentifier = ServiceArea.HubIdentifier\n" +
                "            AND HubRepair.InService = FALSE\n" +
                "            order by AreaServedByHub desc limit " + limit + ";");
        str = new ArrayList<>();

        while (resultSet.next()) {
            str.add(resultSet.getString("servicedAreas"));
        } statement.close();}
        catch (SQLException e) {
        throw new RuntimeException(e);
    }
        return str;
    }
}
