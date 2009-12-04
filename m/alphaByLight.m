## Calculate alpha and e for given image  and illumination(L, W)

function [ALPHA, E] = alphaByLight(NN, L, W)
  BB=calcBasis(NN);
  IMG = weightedDiffuse(NN, L, W);
  [ALPHA, E] = solveOLS(IMG, BB);
end