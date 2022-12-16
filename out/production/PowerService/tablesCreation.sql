use mpandey;

CREATE TABLE AddPostalCode (
	PostalCode VARCHAR(100) NOT NULL,
	Population INT,
	Area INT,
    PRIMARY KEY (PostalCode)
);

CREATE TABLE AddDistributionHub (
	HubIdentifier VARCHAR(100) NOT NULL,
	Location VARCHAR(100),
    PRIMARY KEY (HubIdentifier)
);

CREATE TABLE ServiceArea (
	HubIdentifier VARCHAR(100) NOT NULL,
	servicedAreas VARCHAR(100),
    FOREIGN KEY (HubIdentifier) REFERENCES AddDistributionHub(HubIdentifier)
    );

CREATE TABLE HubDamage (
	HubIdentifier VARCHAR(100) NOT NULL,
	RepairEstimate FLOAT,
	FOREIGN KEY (HubIdentifier) REFERENCES AddDistributionHub(HubIdentifier)
    );

CREATE TABLE HubRepair(
	HubIdentifier VARCHAR(100) NOT NULL,
    employeeId VARCHAR(100) NOT NULL,
	RepairTime FLOAT,
    InService BOOLEAN,
    FOREIGN KEY (HubIdentifier) REFERENCES AddDistributionHub(HubIdentifier)
);