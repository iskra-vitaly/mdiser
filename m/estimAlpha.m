## estimate alpha column vector for given image

function [alphaTra] = estimAlpha(workDir, Itra) 
  fileNames = glob(sprintf("%s/*.jpg", workDir));
  K = length(fileNames);
  W = zeros(K, 1);
  load(sprintf("%s/bootstrap.data", workDir), "bMean", "bCov", "alpha");
  for k=1:K
    imgK = imread(fileNames(k));
    Ik = imgK(:);
    diff = (ITra-Ik);
    D = diff'*diff;
    
  endfor
endfunction