## calculate sigmaS for all images of given subject
## function [sigmaS] = calcSigmaS(imgDir, subj, tolerance, dim)
## imgDir    - directory with organized bootstrap image set
## subj      - subject number
## tolerance - sigma tolerance factor 
## dim = struct {
##    subjects   - subjects count
##    conditions - conditions count
##    D          - image pixel count
## }

function [sigmaS, loadTime, calcTime, sigmaTime, DSUBJ] = calcSigmaS (imgDir, cond, tolerance, dim)
  IMGS = zeros(dim.D, dim.subjects);
  loadTime = cputime();
  for k=1:dim.subjects
    imgK = imread(sprintf("%s/bysubj/%02d/%04d.jpg", imgDir, k, cond));
    IMGS(:,k) = double(imgK(:))/255.0;
  endfor
  loadTime = cputime()-loadTime;
  [sigmaS, calcTime, sigmaTime, DSUBJ] = calcSigma(IMGS, tolerance);  
endfunction