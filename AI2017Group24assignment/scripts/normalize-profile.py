#!/usr/bin/python

import sys
import xml.etree.ElementTree

def odef(o, k, v, b = False):
  if k not in o:
    o[k] = v
  return o[k] if b else o

def handleIssue(issue, obj):
  evals = []
  for item in issue:
    if item.tag == 'item':
      name = item.attrib['value']
      evaluation = item.attrib['evaluation']
      odef(obj['items'], name, { 'evaluation': float(evaluation), 'normalized': 0 })
      evals.append({ 'name': name, 'evaluation': evaluation })
  mx = max(evals, key=lambda o: o['evaluation'])['evaluation']
  for o in evals:
    obj['items'][o['name']]['normalized'] = float(o['evaluation']) / float(mx)

def findByIndex(o, i):
  for k in o:
    if o[k]['index'] == i:
      return k
  return None

def handleObjective(child, obj):
  for issue in child:
    if issue.tag == 'issue':
      name = issue.attrib['name']
      index = issue.attrib['index']
      handleIssue(issue, odef(obj, name, { 'index': index, 'weight': 0, 'items': {} }, True))
    elif issue.tag == 'weight':
      index = issue.attrib['index']
      name = findByIndex(obj, index)
      if name != None:
        obj[name]['weight'] = issue.attrib['value']

def handleUtilitySpace(root):
  total = {}

  for child in root:
    if child.tag == 'objective':
      name = child.attrib['name']
      handleObjective(child, odef(total, name, {}, True))

  return total

root = xml.etree.ElementTree.parse(sys.argv[1]).getroot()

total = handleUtilitySpace(root)

for obj in total:
  print obj
  for issue in total[obj]:
    print issue, total[obj][issue]['weight']
    for item in total[obj][issue]['items']:
      print item, total[obj][issue]['items'][item]['evaluation'], total[obj][issue]['items'][item]['normalized']
    print
  print
