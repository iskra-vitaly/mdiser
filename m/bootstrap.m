## analyze bootstap images

function bootstrap(dbDir, matDir, m, w, h, nb)
  bTotal = NaN;
  B = zeros(nb, w*h, m);
  for k=1:m
    subjectName = sprintf("%02d", k);
    subjectDir = sprintf("%s/%s/%s", dbDir, subjectName, matDir);
    normMat = readNorm(subjectDir);
    bMat = calcBasis(normMat);
    normV = normMat(:,:);
    bV = bMat(:,:);
    if isnan(bTotal)
      bTotal = bV;
    else
      bTotal = [bTotal bV];
    endif
    B(:, :, k) = bV;
  endfor
  bMean = zeros(nb, w*h);
  bMean = mean(B, 3);
  C(1) = cov()
end

function showVec(V, w, h)
  VV = zeros(h, w);
  VV(:) = V(:);
  imshow(VV);
end