

fsim = open("countriesSimilarity.txt", "rt", encoding='utf-8')
simDict = {}
for row in fsim:
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
        temp[1] = codeDict[simDict[key]]
    except:
        try:
            temp[1] = codeDict2[simDict[key]]
        except:
            print(key)
    simCodeDict[temp[0]] = temp[1]

print(simCodeDict)
