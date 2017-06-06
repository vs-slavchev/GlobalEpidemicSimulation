import requests
import re
from lxml import html
import unicodedata

pageContent = requests.get('https://en.wikipedia.org/wiki/List_of_countries_and_territories_by_land_borders')
tree = html.fromstring(pageContent.content)
countriesRows = tree.xpath('//table/tr/td[1]/b/a/text()/ancestor::tr')

find_pattern = re.compile(r"\s*([\d,]+)(\.\d+)?\s*km")
outputFile = open('countriesSimilarity.txt', 'w+', encoding="utf-8")
outputFile2 = open('countryBorders.txt.', 'w+', encoding="utf-8")

for countryRow in countriesRows:
    # .// specifies a relative path
    name = countryRow.xpath('.//td[1]/b/a/text()')
    neighbours = countryRow.xpath('.//td[5]//a/text()')
    array_of_kilometer_strings = countryRow.xpath('.//td[5]//div[@class="NavContent"]/text()')

    # print(array_of_kilometer_strings)

    def is_clean(text):
        return all(weird_char not in text for weird_char in ['[', ':', '(', ')', u'\n', r'\n', '\n', '\\'])

    def extract_clean_information(single_string):
        transformed_string = unicodedata.normalize('NFKD', single_string)
        transformed_string = transformed_string.rstrip()

        pieces = transformed_string.split(" ")
        pieces = list(filter(is_clean, pieces))
        return " ".join(pieces)

    kilometers_clean = []
    for countryKilometers in array_of_kilometer_strings:
        clean_kilometers = extract_clean_information(countryKilometers)
        if (clean_kilometers):
            kilometers_clean.append(clean_kilometers)

    neighbours_clean = []
    for neighbour in neighbours:
        clean_neighbour = extract_clean_information(neighbour)
        if (clean_neighbour):
            neighbours_clean.append(clean_neighbour)

    s = ("\n" + name[0] + " ###\n")
    outputFile2.write(s)
	
    infoNeighbour = ""
    for neighbourName, neighbourKilometers in zip(neighbours_clean, kilometers_clean):
        # keep the digits for the kilometers only
        neighbourKilometers = find_pattern.sub(r"\1", neighbourKilometers)
        infoNeighbour += str(neighbourName + " " + neighbourKilometers + "\n")

    outputFile2.write(infoNeighbour)
	
    tupleNeighbourNameKilometers = "", 0
    for neighbourName, neighbourKilometers in zip(neighbours_clean, kilometers_clean):
        # keep the digits for the kilometers only
        neighbourKilometersStr = find_pattern.sub(r"\1", neighbourKilometers)
        try:
            neighbourKilometers = int(neighbourKilometersStr.replace(',', ''))
        except ValueError:
            neighbourKilometers = 0
        # infoNeighbour += str(neighbourName + " " + str(neighbourKilometers) + "\n")

        if (tupleNeighbourNameKilometers[1] < neighbourKilometers):
            tupleNeighbourNameKilometers = (neighbourName, neighbourKilometers)

    # print(infoNeighbour + "------ closest neighbour: " + tupleNeighbourNameKilometers[0])
    if tupleNeighbourNameKilometers[0]:
        outputLine = name[0] + " > " + tupleNeighbourNameKilometers[0]
        print(outputLine)
        outputFile.write(outputLine + '\n')

outputFile.close()
outputFile2.close()
