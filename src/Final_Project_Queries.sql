#AddPostalCodes
insert into AddPostalCode values('B3L3P3',10,20);

#AddDistributionHub
insert into AddDistributionHub (HubIdentifier,Location) values('H1','1,2');
insert into ServiceArea (HubIdentifier,servicedAreas) values('H1','B3L3P3');

#HubDamage
insert into HubDamage values('H1',1);

#hubRepair
#Query 1
insert into HubRepair values('H1','E1',1,false);

#Query 2
UPDATE HubDamage 
SET 
    RepairEstimate = RepairEstimate - RepairTime
WHERE
    HubIdentifier IN ('');

#Query 3
UPDATE HubRepair
        INNER JOIN
    (SELECT 
        HubRepair.HubIdentifier, RepairEstimate, InService
    FROM
        HubRepair
    LEFT JOIN HubDamage ON HubRepair.HubIdentifier = HubDamage.HubIdentifier) a ON HubRepair.HubIdentifier = a.HubIdentifier 
SET 
    HubRepair.InService = TRUE
WHERE
    HubRepair.HubIdentifier = 'a1'
        AND a.RepairEstimate = 0;
  
  #peopleOutOfService
  
  #Query 1
  SELECT 
    SUM(a.populationServedByHub) AS pos
FROM
    (SELECT DISTINCT
        ServiceArea.HubIdentifier,
            ServiceArea.servicedAreas,
            AddPostalCode.Population,
            HubRepair.Inservice,
            c,
            AddPostalCode.Population / c AS populationServedByHub
    FROM
        ServiceArea, HubRepair, AddPostalCode, (SELECT DISTINCT
        servicedAreas, COUNT(HubIdentifier) AS c
    FROM
        ServiceArea
    GROUP BY servicedAreas) sub
    WHERE
        ServiceArea.servicedAreas = AddPostalCode.PostalCode
            AND sub.servicedAreas = ServiceArea.servicedAreas
            AND HubRepair.HubIdentifier = ServiceArea.HubIdentifier
            AND HubRepair.InService = FALSE) a;
    
#mostDamagedPostalCodes                                        
                                                               
#Query 1                  

select  ServiceArea.servicedAreas, sum(HubDamage.RepairEstimate) as damaged from ServiceArea 
left join HubDamage on ServiceArea.HubIdentifier=HubDamage.HubIdentifier 
group by ServiceArea.servicedAreas order by damaged desc limit 5;


#fixOrder

SELECT         
a.HubIdentifier,  
SUM(a.populationServedByHub)/HubDamage.RepairEstimate 
ASimpact 
        FROM
            (SELECT DISTINCT
               ServiceArea.HubIdentifier,
                    ServiceArea.servicedAreas,
                    AddPostalCode.Population,
                    HubRepair.Inservice,   c,    AddPostalCode.Population / c AS populationServedByHub 
            FROM   ServiceArea, HubRepair, AddPostalCode, (SELECT DISTINCT
                servicedAreas, COUNT(HubIdentifier) AS c
            FROM
                ServiceArea   GROUP BY servicedAreas) sub
            WHERE    ServiceArea.servicedAreas = AddPostalCode.PostalCode AND HubRepair.InService = false
                   AND sub.servicedAreas = ServiceArea.servicedAreas  AND HubRepair.HubIdentifier = ServiceArea.HubIdentifier   ) a , HubDamage where a.HubIdentifier=HubDamage.HubIdentifier 
                     group by a.HubIdentifier order by impact desc;

#underservedPostalByPopulation

SELECT DISTINCT
            ServiceArea.servicedAreas,
            AddPostalCode.Area,
            HubRepair.Inservice,
            c,
            AddPostalCode.Area / c AS populationServedByHub
    FROM
        ServiceArea, HubRepair, AddPostalCode, (SELECT DISTINCT
        servicedAreas, COUNT(HubIdentifier) AS c
    FROM
        ServiceArea
    GROUP BY servicedAreas) sub
    WHERE
        ServiceArea.servicedAreas = AddPostalCode.PostalCode
            AND sub.servicedAreas = ServiceArea.servicedAreas
            AND HubRepair.HubIdentifier = ServiceArea.HubIdentifier
            AND HubRepair.InService = FALSE
            order by populationServedByHub desc limit 3;

#underservedPostalByArea

SELECT DISTINCT
            ServiceArea.servicedAreas,
            AddPostalCode.Area,
            HubRepair.Inservice,
            c,
            AddPostalCode.Area / c AS AreaServedByHub
    FROM
        ServiceArea, HubRepair, AddPostalCode, (SELECT DISTINCT
        servicedAreas, COUNT(HubIdentifier) AS c
    FROM
        ServiceArea
    GROUP BY servicedAreas) sub
    WHERE
        ServiceArea.servicedAreas = AddPostalCode.PostalCode
            AND sub.servicedAreas = ServiceArea.servicedAreas
            AND HubRepair.HubIdentifier = ServiceArea.HubIdentifier
            AND HubRepair.InService = FALSE
            order by AreaServedByHub desc limit 3;
            
