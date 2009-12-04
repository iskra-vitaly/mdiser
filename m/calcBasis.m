## Calculate basis Functions

function [B] = calcBasis(N)
  wh = size(N);
  B = zeros(9, wh(2), wh(3));
  msk(:,:) = dot(N, N, 1)>0.9;
  B(1,:,:) = 1/sqrt(4*pi);
  B(2,:,:) = sqrt(3/(4*pi))*N(3,:,:);
  B(3,:,:) = sqrt(3/(4*pi))*N(2,:,:);
  B(4,:,:) = sqrt(3/(4*pi))*N(1,:,:);
  B(5,:,:) = 0.5*sqrt(3/(4*pi))*(2*N(3,:,:).^2-N(1,:,:).^2-N(2,:,:).^2);
  B(6,:,:) = 3*sqrt(5/(12*pi))*(N(1,:,:).*N(3,:,:));
  B(7,:,:) = 3*sqrt(5/(12*pi))*(N(2,:,:).*N(3,:,:));
  B(8,:,:) = 3/2*sqrt(5/(12*pi))*(N(1,:,:).^2 - N(2,:,:).^2);
  B(9,:,:) = 3*sqrt(5/(12*pi))*(N(1,:,:).*N(2,:,:));
  for i=1:9
    BMat(:,:) = B(i,:,:);
    B(i,:,:)=msk.*BMat;
  end
end