## calculate sigmaC for all images of given subject
## function [sigmaC] = calcSigmaC(imgDir, subj, tolerance, dim)
## imgDir    - directory with organized bootstrap image set
## subj      - subject number
## tolerance - sigma tolerance factor 
## dim = struct {
##    subjects   - subjects count
##    conditions - conditions count
##    D          - image pixel count
## }

function [sigmaC, loadTime, calcTime, sigmaTime, DCOND] = calcSigmaC (imgDir, subj, tolerance, dim)
  IMGS = zeros(dim.D, dim.conditions);
  loadTime = cputime();
  for k=1:dim.conditions
    imgK = imread(sprintf("%s/bysubj/%02d/%04d.jpg", imgDir, subj, k));
    IMGS(:,k) = double(imgK(:))/255.0;
  endfor
  loadTime = cputime()-loadTime;
  [sigmaC, calcTime, sigmaTime, DCOND] = calcSigma(IMGS, tolerance);  
endfunction