#!/usr/bin/python

import sys
import xml.etree.ElementTree

def handleResult(total, result):
  at = result.attrib
  agentClass = at['agentClass']
  total['agents'].add(agentClass)
  finalUtility = float(at['finalUtility'])
  if agentClass not in total['utilities']:
    total['utilities'][agentClass] = 0.0
  total['utilities'][agentClass] += finalUtility

def handleOutcome(total, outcome):
  total['rounds'] += 1
  children = []
  for child in outcome:
    if child.tag == 'resultsOfAgent':
      handleResult(total, child)
      children.append(child)
  
  agentWin = max(children, key=lambda t: t.attrib['finalUtility']).attrib['agentClass']
  if agentWin not in total['wins']:
    total['wins'][agentWin] = 0
  total['wins'][agentWin] += 1

def maxVal(d):
  return max(d.items(), key=lambda t: t[1])

def handleTournament(root):

  total = {
    'rounds': 0,
    'agents': set(),
    'utilities': {},
    'wins': {},
    'welfare': 0,
    'average': 0,
    'maxAgent': None,
    'maxUtility': 0,
    'winAgent': None,
    'maxWins': 0
  }

  for child in root:
    if child.tag == 'NegotiationOutcome':
      handleOutcome(total, child)
  for agent in total['utilities']:
    total['utilities'][agent] /= total['rounds']
    total['welfare'] += total['utilities'][agent]
    total['average'] += total['utilities'][agent]

  total['average'] /= len(total['agents'])

  maxU = maxVal(total['utilities'])
  total['maxAgent'] = maxU[0]
  total['maxUtility'] = maxU[1]

  maxW = maxVal(total['wins'])
  total['winAgent'] = maxW[0]
  total['maxWins'] = maxW[1]

  return total

###
root = xml.etree.ElementTree.parse(sys.argv[1]).getroot()

total = handleTournament(root)

print 'Rounds:', total['rounds']
print 'Welfare:', total['welfare']
print 'Average:', total['average']
print 'Highest average utility:', total['maxAgent'], 'with', total['maxUtility'], 'utility'
print 'Winning agent:', total['winAgent'], 'with', total['maxWins'], 'wins'
print
for agent in total['agents']:
  print agent
  print "Average utility:", total['utilities'][agent]
  print "Amount of wins:", total['wins'][agent]
  print
