import csv
f = open("iso_3166_2_countries.csv", "rt", encoding="utf-8") #file contains all sorts of identification formats for each country
fr = csv.reader(f, delimiter=',')
codeDict = {} #used format - '2 letter code': '3 letter code'
for row in fr:
    if(len(row[11]) == 3):      #only adds if there is a 2 letter ISO 3166-1 code; skips 1 country without 2 annd 3 letter ISO 3166-1 codes and the first row
        codeDict[row[1]] = row[11]

f.seek(0)
codeDict2 = {}
for row in fr:
    if(len(row[11]) ==3):
       codeDict2[row[2]] = row[11]

countriesNested = [] #nested list of all countries; Latest country format: [Name, Code, Population, Population Density, Government Form]
tempCountry = []

fsim = open("countriesSimilarity.txt", "rt", encoding='utf-8')
simDict = {}
for row in simDict:
    try:
        temp = row.split(' > ')
        simDict[temp[0]] = temp[1][:-1]
    except:
        continue

simCodeDict = {}
for key in simDict.keys():
    temp = ["", ""]
    try:
        temp[0] = codeDict[key]
    except:
        try:
            temp[0] = codeDict2[key]
        except:
            print(key)
    try:
        temp[1] = codeDict[res[key]]
    except:
        try:
            temp[1] = codeDict2[res[key]]
        except:
            print(key)
    simCodeDict[temp[0]] = temp[1]

