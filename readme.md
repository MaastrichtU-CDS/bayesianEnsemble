# Federated Bayesian Ensembles

This project implements a network of bayesian ensembles that specificly makes use of the federated nature of the data to creates
 its ensembles.
It uses two libraries:
n-party scalar-product: https://github.com/MaastrichtU-CDS/n-scalar-product-protocol
VertiBayes: https://github.com/MaastrichtU-CDS/vertibayes

## Preprint:

A preprint is currently being prepared.

#### Privacy
The privacy concerns are similar to VertiBayes this means the following:

This implementation relies on the n-party scalar product protocol mentioned earlier, and as such relies on a trusted
third party, a different scalar product protocol could work without a trusted third party.

The output of this implementation is a bayesian network. It is important to note that a bayesian network reveals P(
X=xi|Y=yi) for each of it's attributes. Combined with knowledge about the population size this means that publishing a
bayesian network can reveal the counts of certain attribute values, as well as the counts of certain combinations. This
information can potentially be used to reconstruct the original database. Because of this it is important to make sure
when publishing the bayesian network that the population size is not released to untrusted parties.

It is important to note that repeat attempts to build a network with different predefined bins & different predefined
network structures can be combined to reveal more information than a single attempt, making it easier to rebuild the
original data. To protect against this good governance should be used. For example, only allow automatically generated
structures & bins, ensuring each attempt results in the same network. Or by ensuring that only the best performing
bayesian network is published, while the other attempts are kept secret.

## Project setup:

This project uses Spring boot at its basis. The following properties are needed:

```
servers=<list of urls for the other servers>
secretServer=<url to the commodity server>
datapath=<path to a csv containing local data>
server.port=<port to be used>
server=<server id>
```

A dockerFile is included to wrap the project for use in vantage6.

## Data setup:
The data can be presented in three formats:
1) A weka .arff
2) A .parquet file
3) A .csv file

The vantage6 wrapper assumes a .csv file. Work is currently being done to allow more flexibility within the wrapper.
https://github.com/vantage6/vantage6/issues/398

When using a csv it needs to use the following format. The top row is assumed to contain the typing of the
attributes (bool, string, numeric, real). The second row is assumed to be attribute ID's. We also assume the first
collumn to contain ID's. The assumption is that the first collumn contains the recordId's.

### Unknown data

It is assumed unknown data has the value of '?'

### Other assumptions:

It is assumed values do not contain spaces, similarly it is assumed that the various keywords in a WEKA bif file are not
used, as well as those used in a WEKA arff file.

Lastly "All" is a reserved keyword for a bin that contains all possible values for a given attribute.

#### Handling a Hybird split

To handle a Hybrid split in your data include an attributecolumn in all relevant datasets named "locallyPresent" with "
bool" as it's type. Locally available data should have the value "TRUE". Missing records are then inserted as a row that
has the value "FALSE" for this attribute. This should be handled in a preprocessing step.

Important to note; datasets still need to have the same ordering for their records. It is assumed that recordlinkage is
handled in a preprocessing step as well.

## Implemented methods:

createEnsemble:
This method creates an ensemble of bayesian networks.
It can either use predefined structures or determine them themselves using K2.

### Request example Expectation Maximization

The request for createEnsemble looks as follows:

```
{
  "target" : "x1",
  "networks" : {
    "2" : [ {
      "parents" : [ ],
      "name" : "x1",
      "type" : "string",
      "probabilities" : [ ],
      "bins" : [ ]
    }, {
      "parents" : [ "x1" ],
      "name" : "x3",
      "type" : "string",
      "probabilities" : [ ],
      "bins" : [ ]
    } ]
  },
  "binned" : [ {
    "parents" : [ ],
    "name" : "x1",
    "type" : "numeric",
    "probabilities" : [ ],
    "bins" : [ {
      "upperLimit" : "1.5",
      "lowerLimit" : "0.5"
    }, {
      "upperLimit" : "0.5",
      "lowerLimit" : "-0.5"
    } ]
  }, {
    "parents" : [ ],
    "name" : "x2",
    "type" : "numeric",
    "probabilities" : [ ],
    "bins" : [ {
      "upperLimit" : "1.5",
      "lowerLimit" : "0.5"
    }, {
      "upperLimit" : "0.5",
      "lowerLimit" : "-0.5"
    } ]
  }, {
    "parents" : [ ],
    "name" : "x3",
    "type" : "string",
    "probabilities" : [ ],
    "bins" : [ ]
  } ],
  "hybrid" : false,
  "minPercentage" : 0,
  "folds" : 1
}
```
#### Bins:
Binned is used to indicate bins for relevant nodes. If these are not indicated they will be determined automatically at which point a
maximum of 10 bins will be made, and a minimum of 1 bin. Each bin will attempt to pick the smallest unique values that
contain at least 10 individuals and 10% of the population. This is the default setting. It is also possible to create
bins a minimum of 20%, 25%, 30% or 40% of the population. Other settings are not possible. If the current bin cannot be
made large enough to achieve this it will be merged with the last bin. This percentage is set using minPercentage.

#### Network structure
Networks can be used to predefine network structures. They are assigned to specific parties as key-value pairs, where the key corresponds to the parties ID.
If no network is provided K2 is used to determine the local structure.

#### Hybrid 
Hybrid can be used to indicate if there are hybrid attributes

### Response example:

```
{
  "networks" : [ "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n<ProbModelXML formatVersion=\"0.2.0\">\r\n  <ProbNet type=\"BayesianNetwork\">\r\n    <DecisionCriteria>\r\n      <Criterion name=\"---\" unit=\"---\"/>\r\n    </DecisionCriteria>\r\n    <Properties/>\r\n    <Variables>\r\n      <Variable name=\"x1\" role=\"chance\" type=\"finiteStates\">\r\n        <States>\r\n          <State name=\"0\"/>\r\n          <State name=\"1\"/>\r\n        </States>\r\n      </Variable>\r\n      <Variable name=\"x2\" role=\"chance\" type=\"finiteStates\">\r\n        <States>\r\n          <State name=\"0\"/>\r\n          <State name=\"1\"/>\r\n        </States>\r\n      </Variable>\r\n    </Variables>\r\n    <Links>\r\n      <Link directed=\"true\">\r\n        <Variable name=\"x1\"/>\r\n        <Variable name=\"x2\"/>\r\n      </Link>\r\n    </Links>\r\n    <Potentials>\r\n      <Potential role=\"conditionalProbability\" type=\"Table\">\r\n        <Variables>\r\n          <Variable name=\"x1\"/>\r\n        </Variables>\r\n        <Values>0.5120987901209879 0.4879012098790121</Values>\r\n      </Potential>\r\n      <Potential role=\"conditionalProbability\" type=\"Table\">\r\n        <Variables>\r\n          <Variable name=\"x2\"/>\r\n          <Variable name=\"x1\"/>\r\n        </Variables>\r\n        <Values>0.7935376805935181 0.20646231940648185 0.19579918032786886 0.8042008196721312</Values>\r\n      </Potential>\r\n    </Potentials>\r\n  </ProbNet>\r\n  <InferenceOptions>\r\n    <MulticriteriaOptions>\r\n      <SelectedAnalysisType>UNICRITERION</SelectedAnalysisType>\r\n      <Unicriterion>\r\n        <Scales>\r\n          <Scale Criterion=\"---\" Value=\"1.0\"/>\r\n        </Scales>\r\n      </Unicriterion>\r\n      <CostEffectiveness>\r\n        <Scales>\r\n          <Scale Criterion=\"---\" Value=\"1.0\"/>\r\n        </Scales>\r\n        <CE_Criteria>\r\n          <CE_Criterion Criterion=\"---\" Value=\"Cost\"/>\r\n        </CE_Criteria>\r\n      </CostEffectiveness>\r\n    </MulticriteriaOptions>\r\n  </InferenceOptions>\r\n</ProbModelXML>\r\n", "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n<ProbModelXML formatVersion=\"0.2.0\">\r\n  <ProbNet type=\"BayesianNetwork\">\r\n    <DecisionCriteria>\r\n      <Criterion name=\"---\" unit=\"---\"/>\r\n    </DecisionCriteria>\r\n    <Properties/>\r\n    <Variables>\r\n      <Variable name=\"x3\" role=\"chance\" type=\"finiteStates\">\r\n        <States>\r\n          <State name=\"0\"/>\r\n          <State name=\"1\"/>\r\n        </States>\r\n      </Variable>\r\n      <Variable name=\"x1\" role=\"chance\" type=\"finiteStates\">\r\n        <States>\r\n          <State name=\"0\"/>\r\n          <State name=\"1\"/>\r\n        </States>\r\n      </Variable>\r\n    </Variables>\r\n    <Links>\r\n      <Link directed=\"true\">\r\n        <Variable name=\"x3\"/>\r\n        <Variable name=\"x1\"/>\r\n      </Link>\r\n    </Links>\r\n    <Potentials>\r\n      <Potential role=\"conditionalProbability\" type=\"Table\">\r\n        <Variables>\r\n          <Variable name=\"x3\"/>\r\n        </Variables>\r\n        <Values>0.4072092790720928 0.5927907209279072</Values>\r\n      </Potential>\r\n      <Potential role=\"conditionalProbability\" type=\"Table\">\r\n        <Variables>\r\n          <Variable name=\"x1\"/>\r\n          <Variable name=\"x3\"/>\r\n        </Variables>\r\n        <Values>0.7575497176528357 0.24245028234716426 0.3289762185866082 0.6710237814133918</Values>\r\n      </Potential>\r\n    </Potentials>\r\n  </ProbNet>\r\n  <InferenceOptions>\r\n    <MulticriteriaOptions>\r\n      <SelectedAnalysisType>UNICRITERION</SelectedAnalysisType>\r\n      <Unicriterion>\r\n        <Scales>\r\n          <Scale Criterion=\"---\" Value=\"1.0\"/>\r\n        </Scales>\r\n      </Unicriterion>\r\n      <CostEffectiveness>\r\n        <Scales>\r\n          <Scale Criterion=\"---\" Value=\"1.0\"/>\r\n        </Scales>\r\n        <CE_Criteria>\r\n          <CE_Criterion Criterion=\"---\" Value=\"Cost\"/>\r\n        </CE_Criteria>\r\n      </CostEffectiveness>\r\n    </MulticriteriaOptions>\r\n  </InferenceOptions>\r\n</ProbModelXML>\r\n" ],
  "aucs" : {
    "0" : 0.78,
    "1" : 0.78
  }
}
```

The repsonse contains the network in the bif format used by openMarkov.

### Crossfold validation:
Crossfold validation is automaticly executed if folds >1. The maximum number of folds is 10.
