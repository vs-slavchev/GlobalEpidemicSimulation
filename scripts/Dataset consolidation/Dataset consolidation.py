#Generates a dictionary for 2/3 letter ISO 3166-1 country codes
#All
import csv
f = open("iso_3166_2_countries.csv", "rt", encoding="utf-8") #file contains all sorts of identification formats for each country
fr = csv.reader(f, delimiter=',')
codeDict = {} #used format - '2 letter code': '3 letter code'
for row in fr:
    if(len(row[10]) == 2):      #only adds if there is a 2 letter ISO 3166-1 code; skips 1 country without 2 annd 3 letter ISO 3166-1 codes and the first row
        codeDict[row[10]] = row[11]

countriesNested = [] #nested list of all countries; Latest country format: [Name, Code, Population, Population Density, Government Form]
tempCountry = []     #temporary holder for countries to be appended to countriesNested;

#Extracts all relevant information from countries.csv
#Country format after this block: [Name, Code, Population, Government Form]
f2 = open("countries.csv", "rt", encoding="utf-8")
f2r = csv.reader(f2, delimiter=';')
for row in f2r:
    if(row[3] in codeDict.keys()):
        tempCountry.append(row[0])  #Appends the country name to the temp country
        tempCountry.append(codeDict[row[3]])    #Appends the country 3 letter code to the temp country
        tempCountry.append('3'+row[6])  #Appends the country population to the temp country; Adds a 3 at the front to identify which element it is as per standard country format
        tempCountry.append('5'+row[9])  #Appends the country government form to the temp country; Adds a 5 at the front to identify which element it is as per standard country format
        countriesNested.append(tempCountry) #Adds the country to the list
        tempCountry = [] #Reassigns a null list to TempCountry; Note: Some list clear methods also wipe all references and would affect the country in countriesNested too

#Extracts country population density data (2010) from countries-population-density.csv
#Country format after this block: [Name, Code, Population, Population Density, Government Form]
f3 = open("countries-population-density.csv", "rt", encoding="utf-8")
f3r = csv.reader(f3, delimiter=';')
for row in f3r:
    for country in countriesNested:
        if(country[1] == row[1]):
            country.insert(3,'4'+row[2])    #Inserts the country population density at index 3; Adds a 4 at the front to identify which element it is as per standard country format

#Extracts country air pollution data for the latest available year from air-pollution.csv
#Country format after this block: [Name, Code, Population, Population Density, Government Form, Air Pollution]
f4 = open("air-pollution.csv", "rt", encoding='utf-8')
f4r = csv.reader(f4,delimiter=',')
for row in f4r:
        try:
            if(float([item for item in row if item][-1])):  #Make sure the last non-empty value is a float
                for country in countriesNested:
                    if(row[1] == country[1]):
                        country.append('6'+[item for item in row if item][-1])  #Appends the most recent non-empty air pollution data to the country; Adds a 6 at the front to identify which element it is as per standard country format
        except (ValueError, IndexError) as e:
            pass

#Extracts country public health expenditure data for the latest available year from health-expenditure-public.csv
#Country format after this block: [Name, Code, Population, Population Density, Government Form, Air Pollution, Public Health Expenditure]
f5 = open("health-expenditure-public.csv", "rt", encoding='utf-8')
f5r = csv.reader(f5, delimiter=',')
for row in f5r:
        try:
            if(float([item for item in row if item][-1])):  #Make sure the last non-empty value is a float
                for country in countriesNested:
                    if(row[1] == country[1]):
                        country.append('7'+[item for item in row if item][-1]) #Appends the most recent non-empty country public health expenditure data to the country; Adds a 7 at the front to identify which element it is as per standard country format
        except (ValueError, IndexError) as e:
            pass

#Extracts country health expenditure per capita data for the latest available year from health-expenditure-public.csv
#Country format after this block: [Name, Code, Population, Population Density, Government Form, Air Pollution, Public Health Expenditure, Health Expenditure per Capita]
f6 = open("health-expenditure-per-capita.csv", "rt", encoding='utf-8')
f6r = csv.reader(f6, delimiter=',')
for row in f6r:
        try:
            if(float([item for item in row if item][-1])):  #Make sure the last non-empty value is a float
                for country in countriesNested:
                    if(row[1] == country[1]):
                        country.append('8'+[item for item in row if item][-1])  #Appends the most recent non-empty country health expenditure per capita data to the country; Adds a 8 at the front to identify which element it is as per standard country format
        except (ValueError, IndexError) as e:
            pass

#Saves the current state of countriesNested in a .txt file; Overwrites any existing files with the same name
wr = open("country consolidated data.txt", "w+")
for country in countriesNested:
    s = ";".join(country) + '\n'    #joins all members of a country using a ';' delimiter
    wr.write(s)
wr.close()
