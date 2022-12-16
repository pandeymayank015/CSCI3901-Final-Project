import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws SQLException {
PowerService ps=new PowerService();

Point p=new Point(2,3);
Point p1=new Point(1,2);
Point p2=new Point(4,5);
Point p3=new Point(7,5);
Point p4=new Point(4,9);
Point p5=new Point(5,3);
Point p6=new Point(7,1);
Point p7=new Point(5,2);

        Set<String> postalCodes =new HashSet<>();
        postalCodes.add("P2");
        postalCodes.add("P3");

        Set<String> postalCodes1 =new HashSet<>();
        postalCodes1.add("P4");

        Set<String> postalCodes2 =new HashSet<>();
        postalCodes2.add("P1");
        postalCodes2.add("P2");

        Set<String> postalCodes3 =new HashSet<>();
         postalCodes3.add("P4");
         postalCodes3.add("P6");
         postalCodes3.add("P7");
 
         Set<String> postalCodes4 =new HashSet<>();
         postalCodes4.add("P6");
         postalCodes4.add("P8");
         postalCodes4.add("P1");
 
         Set<String> postalCodes5 =new HashSet<>();
         postalCodes5.add("P9");
         postalCodes5.add("P3");
 
         Set<String> postalCodes6 =new HashSet<>();
         postalCodes6.add("P2");
         postalCodes6.add("P4");
         postalCodes6.add("P8");
 
         Set<String> postalCodes7 =new HashSet<>();
         postalCodes7.add("P5");
         postalCodes7.add("P7");

         ps.addPostalCode( "P1",10 ,200 );
         ps.addPostalCode( "P2",30 ,100 );
         ps.addPostalCode( "P3",25 ,50 );
         ps.addPostalCode( "P4",15 ,250 );
         ps.addPostalCode( "P5",20 ,150 );
         ps.addPostalCode( "P6",10 ,100 );
         ps.addPostalCode( "P7",15 ,250 );
         ps.addPostalCode( "P8",35 ,10 );
         ps.addPostalCode( "P9",40 ,300 );

         ps.addDistributionHub("H1",p,postalCodes);
         ps.addDistributionHub("H2",p1,postalCodes1);
         ps.addDistributionHub("H3",p2,postalCodes2);
         ps.addDistributionHub("H4",p3,postalCodes3);
         ps.addDistributionHub("H5",p4,postalCodes4);
         ps.addDistributionHub("H6",p5,postalCodes5);
         ps.addDistributionHub("H7",p6,postalCodes6);
         ps.addDistributionHub("H8",p7,postalCodes7);
 
         ps.hubDamage("H1",10);
         ps.hubDamage("H2",20);
         ps.hubDamage("H3",5);
         ps.hubDamage("H4",25);
         ps.hubDamage("H5",30);
         ps.hubDamage("H6",15);
         ps.hubDamage("H7",10);
         ps.hubDamage("H8",2);
 
         ps.hubRepair("H2","E2",10,false);
         ps.hubRepair("H1","E1",5,false);
         ps.hubRepair("H3","E4",5,true);
         ps.hubRepair("H2","E5",10,true);
         ps.hubRepair("H4","E2",15,false);
         ps.hubRepair("H5","E3",10,false);
         ps.hubRepair("H6","E4",7,false);
         ps.hubRepair("H5","E1",10,false);
         ps.hubRepair("H7","E5",2,false);
         ps.hubRepair("H8","E6",1,false);
         ps.hubRepair("H8","E3",1,true);

         System.out.println("peopleOutOfService: "+ps.peopleOutOfService());
         System.out.println(ps.mostDamagedPostalCodes(2).get(1).getPostalCode());
         System.out.println(ps.fixOrder(5).get(1).getHubId());
         System.out.println("rateOfServiceRestoration: "+ps.rateOfServiceRestoration(0.05f));
         System.out.println("underservedPostalByPopulation: "+ps.underservedPostalByPopulation(2));
         System.out.println("underservedPostalByArea: "+ps.underservedPostalByArea(3));
    }
}