from random import randrange as r
#Generates a dictionary for 2/3 letter ISO 3166-1 country codes
import csv
f = open("iso_3166_2_countries.csv", "rt", encoding="utf-8") #file contains all sorts of identification formats for each country
fr = csv.reader(f, delimiter=',')
codeDict = {} #used format - '2 letter code': '3 letter code'
nameCodeDict = {} #used format - 'Common Name': '3 letter code'
nameCodeDict2 = {} #used format - 'Formal Name': '3 letter code'
for row in fr:
    if(len(row[10]) == 2):      #only adds if there is a 2 letter ISO 3166-1 code; skips 1 country without 2 annd 3 letter ISO 3166-1 codes and the first row
        codeDict[row[10]] = row[11]
        nameCodeDict[row[1]] = row[11]
        nameCodeDict2[row[2]] = row[11]

fsim = open("countriesSimilarity.txt", "rt", encoding='utf-8')
simDict = {} #dictionary of similar countries extracted from countriesSimilarity.txt
for row in fsim:
    try:
        temp = row.split(' > ')
        simDict[temp[0]] = temp[1][:-1]
    except:
        continue

simCodeDict = {} #converted form of simDict, using country codes instead of names
for key in simDict.keys():
    temp = ["", ""]
    try:        #looks for the code of the main (first) country in the common and formal name dictionaries
        temp[0] = nameCodeDict[key]
    except:
        try:
            temp[0] = nameCodeDict2[key]
        except:
            print(key)
    try:        #looks for the code of the secondary country (country that resembles the main one) in the common and formal name dictionaries
        temp[1] = nameCodeDict[simDict[key]]
    except:
        try:
            temp[1] = nameCodeDict2[simDict[key]]
        except:
            print(key)
    simCodeDict[temp[0]] = temp[1]  #adds the entry to the code dictionary after finding both codes

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

#Auto-generates missing country data for [Air Pollution, Public Health Expenditure, Health Expenditure per Capita] copying the values of its biggest neighbor with an up to 10% deviation.
for countryMainCode in simCodeDict.keys():
    for country in countriesNested:
        if countryMainCode == country[1]:
            for dataIndex in range(6,9):
                if not any(data[:1] == str(dataIndex) for data in country):     #checks if no value in the country starts with the given index (and thus the data is missing)
                    for countrySimilar in countriesNested:
                        if simCodeDict[countryMainCode]==countrySimilar[1]:     #finds the most similar country
                            for similarData in countrySimilar:
                                if similarData[:1] == str(dataIndex):
                                    country.append(str(dataIndex)+str(float(similarData[1:])*r(90,111)/100))    #auto-generates and appends the appropriate missing data, attaching its index as per the standard format at the beginning

#Orders the countries' auto-generated data
for country in countriesNested:
    index = countriesNested.index(country)  #finds the index of the currently iterated country
    temp = ["","",""]   #temp list to order generated data; includes 3 null placeholders that will be replaced with either existing or auto-generated [Air Pollution, Public Health Expenditure, Health Expenditure per Capita] data (if possible)
    numItems = 3    #default number of items to check
    try:
        int(country[-3][:1])
    except:
        numItems = 2    #reduced number of items to check for countries that only have population and government form data
    for item in country[-numItems:]:
        try:
            if int(item[:1]) not in range(6,9):
                temp.append(item)   #if the checked item is not part of the data we look to auto-generate if missing, the item is appended to the end of temp
            else:
                temp[int(item[:1])-6] = item    #if the checked item is part of the data we look to auto-generate, and so we don't need to generate this item, it is placed in its position in temp
        except:
            continue
    country = country[:-numItems]+temp[3:]      #recreates country, using the unchecked items as well as any falsely flagged checked items (data we split off of a type that we don't seek to auto-generate)
    for realVal in temp[:3]:                    #checks if the placeholders in temp were replaced with actual values and appends them to the country
        if realVal:                             
            country.append(realVal)
    countriesNested[index] = country            #replaces the country in countriesNested with the ordered form

#Saves the current state of countriesNested in a .txt file; Overwrites any existing files with the same name
wr = open("country consolidated data.txt", "w+")
for country in countriesNested:
    s = ""
    for item in country:
        s+=";"+item     #joins all members of a country using a ';' delimiter
    s+="\n"
    #s = ";".join(country) + '\n'                   started exhibiting problems at some point during missing data generation
    wr.write(s[1:])
wr.close()
