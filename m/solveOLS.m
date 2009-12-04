## calc illumination coeffs as ordinary least square

function [ALPHA,E] = solveOLS(IMG, BB)
  B = BB(:,:);
  II = IMG(:);
# equation: II = B'*ALPHA+E
  [ALPHA, SIGMA, E] = ols(II, B');
  
end