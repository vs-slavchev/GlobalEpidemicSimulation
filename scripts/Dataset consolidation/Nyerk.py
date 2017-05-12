#Generates a dictionary for 2/3 letter ISO 3166-1 country codes
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
        tempCountry.append(row[0])
        tempCountry.append(codeDict[row[3]])
        tempCountry.append(row[6])
        tempCountry.append(row[9])
        countriesNested.append(tempCountry)
        tempCountry = []

#Extracts country population density data (2010) from countries-population-density.csv
#Country format after this block: [Name, Code, Population, Population Density, Government Form]
f3 = open("countries-population-density.csv", "rt", encoding="utf-8")
f3r = csv.reader(f3, delimiter=';')
for row in f3r:
    for country in countriesNested:
        if(country[1] == row[1]):
            country.insert(3,row[2])


#Saves the current state of countriesNested in a .txt file; Overwrites any existing files with the same name
wr = open("country consolidated data.txt", "w+")
for country in countriesNested:
    s = ";".join(country) + '\n'    #joins all members of a country using a ';' delimiter
    wr.write(s)
wr.close()
