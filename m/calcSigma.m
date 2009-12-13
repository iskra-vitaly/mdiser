## function [sigmaC, loadTime, calcTime] = calcSigma(IMGS, tolerance)
## ---------- calculate sigma for each image so that for 
##----------- tolerance * size(IMG, 2) ||J, I|| <= sigma(I)

function [sigmaC, calcTime, sigmaTime, D] = calcSigma(IMGS, tolerance)
  d = size(IMGS, 1);
  m = size(IMGS, 2);
  D = zeros(m, m);
  calcTime = cputime();
  for i=1:m
    IMGCOLS = IMGS(:, i)*ones(1, i);
    DMAT = IMGCOLS-IMGS(:, 1:i);
    Drow = sqrt(sum(DMAT.*DMAT, 1));
    D(1:i, i) = Drow';
    D(i, 1:i) = Drow;
  endfor
  calcTime = cputime() - calcTime;
  sigmaTime = cputime();
  sigmaC = zeros(m, 1);
  sorted = sort(D, 2);
  col = round(min(tolerance, 1)*m);
  if col > 0 
    sigmaC = sorted(:, col);
  endif
  sigmaTime = cputime()-sigmaTime;
endfunction