#!/usr/bin/python

import sys
import csv

def maxVal(d):
  return max(d.items(), key=lambda t: t[1])

def bool(s):
  return s == 'Yes'

def cfloat(n):
  return float(n.replace(',', '.'))

def getAgentName(name):
  return name.split('@')[0]

def init(o, k, d, b = False):
  if k not in o:
    o[k] = d
  return o[k] if b else o

def parseCSV(filename):
  data = {
    'rounds': 0,
    'welfare': 0,
    'average': 0,
    'all': {},
    'agents': set(),
    'allUtilities': {},
    'utilities': {},
    'wins': {},
    'maxAgent': None,
    'maxUtility': 0,
    'winAgent': None,
    'maxWins': 0,
    'agreements': 0,
    'agreeing': 0,
    'pareto': 0,
    'nash': 0
  }
  with open(filename, 'rb') as csvfile:
    next(csvfile)
    reader = csv.reader(csvfile, delimiter=';')
    header = reader.next()
    for h in header:
      data['all'][h] = []
    for row in reader:
      data['rounds'] += 1
      agent1 = getAgentName(row[12])
      agent2 = getAgentName(row[13])
      agent3 = getAgentName(row[14])
      util1 = cfloat(row[15])
      util2 = cfloat(row[16])
      util3 = cfloat(row[17])
      data['agents'].add(agent1)
      data['agents'].add(agent2)
      data['agents'].add(agent3)
      init(data['allUtilities'], agent1, [], True).append(util1)
      init(data['allUtilities'], agent2, [], True).append(util2)
      init(data['allUtilities'], agent3, [], True).append(util3)
      maxutil = max([util1, util2, util3])
      if maxutil == 0:
        pass
      elif maxutil == util1:
        init(data['wins'], agent1, 0)[agent1] += 1
      elif maxutil == util2:
        init(data['wins'], agent2, 0)[agent2] += 1
      elif maxutil == util3:
        init(data['wins'], agent3, 0)[agent3] += 1
      for (i, item) in enumerate(row):
        data['all'][header[i]].append(item)
      
  data['welfare'] = sum(map(cfloat, data['all']['Social Welfare'])) / data['rounds']
  data['average'] = data['welfare'] / len(data['agents'])
  data['utilities'] = {}
  for agent in data['allUtilities']:
    data['utilities'][agent] = sum(data['allUtilities'][agent]) / data['rounds']

  maxU = maxVal(data['utilities'])
  data['maxAgent'] = maxU[0]
  data['maxUtility'] = maxU[1]

  maxW = maxVal(data['wins'])
  data['winAgent'] = maxW[0]
  data['maxWins'] = maxW[1]

  data['agreements'] = sum(map(bool, data['all']['Agreement']))
  data['agreeing'] = sum(map(cfloat, data['all']['#agreeing'])) / data['rounds']
  data['pareto'] = sum(map(cfloat, data['all']['Dist. to Pareto'])) / data['rounds']
  data['nash'] = sum(map(cfloat, data['all']['Dist. to Nash'])) / data['rounds']

  return data

###
total = parseCSV(sys.argv[1])

print 'Rounds:', total['rounds']
print 'Welfare:', total['welfare']
print 'Average:', total['average']
print 'Highest average utility:', total['maxAgent'], 'with', total['maxUtility'], 'utility'
print 'Winning agent:', total['winAgent'], 'with', total['maxWins'], 'wins'
print 'Agreements:', total['agreements'], '/', total['rounds']
print 'Average agreeing:', total['agreeing']
print 'Average distance to Pareto:', total['pareto']
print 'Average distance to Nash:', total['nash']
print
for agent in total['agents']:
  print agent
  print "Average utility:", init(total['utilities'], agent, 0, True)
  print "Amount of wins:", init(total['wins'], agent, 0, True)
  print
