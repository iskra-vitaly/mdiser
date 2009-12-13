## estimate alpha column vector for given image

function [alphaTra, eTra] = estimAlpha(workDir, Itra, sigma) 
  fileNames = glob(sprintf("%s/*.jpg", workDir));
  KK = length(fileNames);
  W = zeros(KK, 1);
  squareSigma = sigma.^2;
  load(sprintf("%s/bootstrap.data", workDir), "bMean", "bCov", "alpha");
  K = 1;
  for kk=1:KK
    tokens = regexp(fileNames(kk), ".*(\\d{2})\\.(\\d{4})\\.jpg$", "tokens");
    if length(tokens)==1
      imgK = imread(fileNames(kk));      
      Ik = double(imgK(:))/255.0;
      diff = (ITra-Ik);
      D = diff'*diff;
      subj = str2num(tokens{1}{1});
      cond = str2num(tokens{1}{2});
      sig = squareSigma(subj, cond)
      W(k++) = exp(-0.5*D/sig);
    endif
  endfor
  
endfunction